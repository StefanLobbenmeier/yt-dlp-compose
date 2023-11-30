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
import io.ktor.utils.io.jvm.javaio.*
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

private val logger = KotlinLogging.logger {}

suspend fun HttpClient.downloadFileWithProgress(
    url: String,
    targetFile: File,
    unzipFile: Boolean = false
): UpdateProcess {
    val progressFlow = MutableStateFlow<UpdateDownloadProgress>(DownloadStarted)
    val updateProcess = UpdateProcess(targetFile.name, progressFlow)

    logger.info { "Starting download to $targetFile from $url" }

    val downloadFile =
        get(url) {
            onDownload { bytesSentTotal, contentLength ->
                val progress = bytesSentTotal.toFloat() / contentLength
                logger.trace { "Download progress $progress" }
                progressFlow.emit(CustomUpdateDownloadProgress(progress))
            }
        }

    withContext(Dispatchers.IO) {
        targetFile.parentFile.mkdirs()

        val executable = PosixFilePermissions.fromString("rwxr-xr-x")
        val permissions = PosixFilePermissions.asFileAttribute(executable)
        Files.deleteIfExists(targetFile.toPath())
        Files.createFile(targetFile.toPath(), permissions)

        if (unzipFile) {
            copyFirstEntryToFile(downloadFile.bodyAsChannel().toInputStream(), targetFile)
        } else {
            downloadFile.bodyAsChannel().copyTo(targetFile.writeChannel())
        }

        logger.info { "Completed download to $targetFile from $url" }
        progressFlow.emit(DownloadCompleted)
    }

    return updateProcess
}

private suspend fun copyFirstEntryToFile(zipInputStream: InputStream, targetFile: File) {
    ZipInputStream(zipInputStream).use {
        if (it.nextEntry != null) {
            it.copyTo(targetFile.writeChannel())
        }
    }
}
