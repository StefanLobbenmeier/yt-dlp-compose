package de.lobbenmeier.stefan.ytdlp

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import de.lobbenmeier.stefan.platform.getPlatform
import kotlin.io.path.pathString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class YtDlp(
    private val configuration: YtDlpConfiguration,
    private val version: YtDlpVersion,
) {

    fun createDownloadItem(url: String): DownloadItem {
        return DownloadItem(this, url).also { it.gatherMetadata() }
    }

    fun run(vararg options: String) {
        CoroutineScope(Dispatchers.IO).launch { runAsync(*options) }
    }

    suspend fun runAsync(
        vararg options: String,
        consumer: suspend (String) -> Unit = { line -> println("process $line") }
    ) {
        val ytDlpBinary =
            getPlatform()
                .binariesFolder
                .resolve("yt-dlp/yt-dlp/2023.11.16")
                .toAbsolutePath()
                .pathString
        val ffmpegBinary =
            getPlatform()
                .binariesFolder
                .resolve("ffbinaries/6.1/ffmpeg")
                .toAbsolutePath()
                .pathString
        val fullOptions =
            arrayOf(
                "--ffmpeg-location",
                ffmpegBinary,
                "-v",
                "--cookies-from-browser",
                "firefox",
                *options)

        val command = arrayOf(ytDlpBinary, *fullOptions).joinToString(separator = " ") { "\"$it\"" }
        println("Start process: $command")

        val res =
            process(
                ytDlpBinary,
                *fullOptions,
                stdout = Redirect.Consume { it.collect(consumer) },
                stderr = Redirect.CAPTURE,
                directory = getPlatform().downloadsFolder.toFile())

        println("Script finished with result=${res.resultCode}")
        val output = res.output.joinToString("\n")
        println("stdout+stderr:")
        println(output)

        if (res.resultCode != 0) {
            throw Exception("yt-dlp indicated error in its response")
        }
    }
}
