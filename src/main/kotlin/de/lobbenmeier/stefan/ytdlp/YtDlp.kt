package de.lobbenmeier.stefan.ytdlp

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process

class YtDlp(private val configuration: YtDlpConfiguration, private val version: YtDlpVersion,) {
    suspend fun download(options: DownloadItem) {
        val res = process(
            version.ytDlpBinary, *options.getOptions(),
            stdout = Redirect.CAPTURE,
            stderr = Redirect.CAPTURE,

            // Allows to consume line by line without delay the provided output.
            consumer = { line -> println("process $line") },
        )

        println("Script finished with result=${res.resultCode}")
        println("stdout+stderr:\n" + res.output.joinToString("\n"))
    }
}