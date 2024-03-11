package de.lobbenmeier.stefan.update.ffmpeg

import de.lobbenmeier.stefan.GithubJson
import de.lobbenmeier.stefan.platform.Platform
import de.lobbenmeier.stefan.update.downloadFile
import de.lobbenmeier.stefan.ytdlp.UpdateDownloadProgress
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import java.io.File
import java.nio.file.Path

class FfmpegReleaseDownloader(
    val downloadDirectory: Path,
    val ffBinariesUrl: String = "https://ffbinaries.com/api/v1/version/6.1"
) {
    suspend fun downloadRelease(
        platform: Platform,
        onFfmpegProgress: suspend (UpdateDownloadProgress) -> Unit,
        onFfprobeProgress: suspend (UpdateDownloadProgress) -> Unit,
    ): List<File> {
        val httpClient = HttpClient { install(ContentNegotiation) { json(GithubJson) } }

        val ffmpegRelease = getFfmpegRelease(httpClient)
        return downloadFfmpegReleaseToFile(
            ffmpegRelease, platform, httpClient, onFfmpegProgress, onFfprobeProgress)
    }

    private suspend fun downloadFfmpegReleaseToFile(
        ffmpegRelease: FfmpegRelease,
        platform: Platform,
        httpClient: HttpClient,
        onFfmpegProgress: suspend (UpdateDownloadProgress) -> Unit,
        onFfprobeProgress: suspend (UpdateDownloadProgress) -> Unit,
    ): List<File> {
        val version = ffmpegRelease.version
        val ffmpegUrl =
            ffmpegRelease.bin[platform.ffmpegPlatform.platformName]
                ?: throw Exception("Platform $platform is not supported by ffmpeg")

        val targetFolder =
            downloadDirectory.resolve("ffbinaries").resolve(version).toAbsolutePath().toFile()

        return listOf(
            httpClient.downloadFile(
                ffmpegUrl.ffmpeg,
                targetFolder.resolve("ffmpeg"),
                unzipFile = true,
                onProgress = onFfmpegProgress),
            httpClient.downloadFile(
                ffmpegUrl.ffprobe,
                targetFolder.resolve("ffprobe"),
                unzipFile = true,
                onProgress = onFfprobeProgress),
        )
    }

    private suspend fun getFfmpegRelease(httpClient: HttpClient): FfmpegRelease {
        val releaseResponse: HttpResponse = httpClient.get(ffBinariesUrl)
        return releaseResponse.body()
    }
}
