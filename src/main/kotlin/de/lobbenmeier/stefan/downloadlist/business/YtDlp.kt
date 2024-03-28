package de.lobbenmeier.stefan.downloadlist.business

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.updater.business.getPlatform
import de.lobbenmeier.stefan.updater.model.Binaries
import kotlin.io.path.pathString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class YtDlp(private val binaries: Binaries, private val settings: Settings) {

    private val semaphore = Semaphore(settings.rateLimit ?: 100)

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
        val ytDlpBinary = binaries.ytDlp.pathString
        val ffmpegBinary = binaries.ffmpeg.pathString
        val fullOptions =
            arrayOf(
                "--ffmpeg-location",
                ffmpegBinary,
                "-v",
                "--compat-opt",
                "manifest-filesize-approx",
                "--cookies-from-browser",
                "firefox",
                *settings.toYtDlpConfiguration(),
                *options,
            )

        val command = arrayOf(ytDlpBinary, *fullOptions).joinToString(separator = " ") { "\"$it\"" }

        val res =
            semaphore.withPermit {
                println("Start process: $command")

                process(
                    ytDlpBinary,
                    *fullOptions,
                    stdout = Redirect.Consume { it.collect(consumer) },
                    stderr = Redirect.CAPTURE,
                    directory = getPlatform().downloadsFolder.toFile()
                )
            }

        println("Script finished with result=${res.resultCode}")
        val output = res.output.joinToString("\n")
        println("stdout+stderr:")
        println(output)

        if (res.resultCode != 0) {
            throw Exception("yt-dlp indicated error in its response")
        }
    }
}
