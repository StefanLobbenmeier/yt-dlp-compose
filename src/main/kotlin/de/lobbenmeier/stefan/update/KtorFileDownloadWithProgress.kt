package de.lobbenmeier.stefan.update

import de.lobbenmeier.stefan.ytdlp.CustomUpdateDownloadProgress
import de.lobbenmeier.stefan.ytdlp.DownloadCompleted
import de.lobbenmeier.stefan.ytdlp.DownloadStarted
import de.lobbenmeier.stefan.ytdlp.UpdateDownloadProgress
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

suspend fun HttpClient.downloadFileWithProgress(url: String, targetFile: File): UpdateProcess {
    val progressFlow = MutableStateFlow<UpdateDownloadProgress>(DownloadStarted)
    val updateProcess = UpdateProcess(targetFile.name, progressFlow)

    coroutineScope {
        val ffmpeg =
            get(url) {
                onDownload { bytesSentTotal, contentLength ->
                    progressFlow.emit(
                        CustomUpdateDownloadProgress(bytesSentTotal.toFloat() / contentLength))
                }
            }

        withContext(Dispatchers.IO) {
            targetFile.parentFile.mkdirs()
            targetFile.createNewFile()
            ffmpeg.bodyAsChannel().copyTo(targetFile.writeChannel())
            progressFlow.emit(DownloadCompleted)
        }
    }

    return updateProcess
}
