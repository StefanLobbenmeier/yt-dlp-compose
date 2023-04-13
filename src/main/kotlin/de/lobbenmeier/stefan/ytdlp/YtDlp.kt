package de.lobbenmeier.stefan.ytdlp

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
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
        val command = arrayOf(version.ytDlpBinary, *options).joinToString(" ")
        println("Start process: $command")

        val res =
            process(
                version.ytDlpBinary,
                *options,
                stdout = Redirect.Consume { it.collect(consumer) },
                stderr = Redirect.CAPTURE,
            )

        println("Script finished with result=${res.resultCode}")
        val output = res.output.joinToString("\n")
        println("stdout+stderr:")
        println(output)

        if (res.resultCode != 0) {
            throw Exception("yt-dlp indicated error in its response")
        }
    }
}
