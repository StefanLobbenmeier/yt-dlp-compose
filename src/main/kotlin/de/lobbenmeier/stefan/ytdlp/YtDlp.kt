package de.lobbenmeier.stefan.ytdlp

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class YtDlp(private val configuration: YtDlpConfiguration, private val version: YtDlpVersion,) {

    fun createDownloadItem(url: String): DownloadItem {
        return DownloadItem(this, url).also {
            it.gatherMetadata()
        }
    }

    fun run(vararg options: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runAsync(*options)
        }
    }

    suspend fun runAsync(vararg options: String) {
        val command = arrayOf(version.ytDlpBinary, *options).joinToString(" ")
        println("Start process: $command")

        val res = process(
            version.ytDlpBinary, *options,
            stdout = Redirect.CAPTURE,
            stderr = Redirect.CAPTURE,

            // Allows to consume line by line without delay the provided output.
            consumer = { line -> println("process $line") },
        )

        println("Script finished with result=${res.resultCode}")
        println("stdout+stderr:\n" + res.output.joinToString("\n"))
    }
}