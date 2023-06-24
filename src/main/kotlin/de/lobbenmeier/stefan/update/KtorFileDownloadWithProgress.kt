package de.lobbenmeier.stefan.update

import de.lobbenmeier.stefan.ytdlp.CustomUpdateDownloadProgress
import de.lobbenmeier.stefan.ytdlp.DownloadCompleted
import de.lobbenmeier.stefan.ytdlp.DownloadStarted
import de.lobbenmeier.stefan.ytdlp.UpdateDownloadProgress
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

private val logger = KotlinLogging.logger {}

suspend fun HttpClient.downloadFileWithProgress(url: String, targetFile: File): UpdateProcess {
    val progressFlow = MutableStateFlow<UpdateDownloadProgress>(DownloadStarted)
    val updateProcess = UpdateProcess(targetFile.name, progressFlow)

    logger.info { "Starting download to $targetFile from $url" }

    val downloadFile =
        get(url) {
            onDownload { bytesSentTotal, contentLength ->
                val progress = bytesSentTotal.toFloat() / contentLength
                logger.info { "Download progress $progress" }
                progressFlow.emit(CustomUpdateDownloadProgress(progress))
            }
        }

    withContext(Dispatchers.IO) {
        targetFile.parentFile.mkdirs()
        targetFile.createNewFile()
        downloadFile.bodyAsChannel().copyTo(targetFile.writeChannel())
        progressFlow.emit(DownloadCompleted)
    }

    return updateProcess
}
