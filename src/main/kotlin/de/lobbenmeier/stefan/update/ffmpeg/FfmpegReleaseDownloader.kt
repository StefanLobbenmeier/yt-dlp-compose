package de.lobbenmeier.stefan.update.ffmpeg

import de.lobbenmeier.stefan.GithubJson
import de.lobbenmeier.stefan.platform.Platform
import de.lobbenmeier.stefan.update.UpdateProcess
import de.lobbenmeier.stefan.update.downloadFileWithProgress
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import java.nio.file.Path

class FfmpegReleaseDownloader(
    val downloadDirectory: Path,
    val ffBinariesUrl: String = "https://ffbinaries.com/api/v1/version/latest"
) {
    suspend fun downloadRelease(platform: Platform): List<UpdateProcess> {
        val httpClient = HttpClient() { install(ContentNegotiation) { json(GithubJson) } }

        val ffmpegRelease = getFfmpegRelease(httpClient)
        return downloadFfmpegReleaseToFile(ffmpegRelease, platform, httpClient)
    }

    private suspend fun downloadFfmpegReleaseToFile(
        ffmpegRelease: FfmpegRelease,
        platform: Platform,
        httpClient: HttpClient
    ): List<UpdateProcess> {
        val version = ffmpegRelease.version
        val ffmpegUrl =
            ffmpegRelease.bin[platform.ffmpegPlatform.platformName]
                ?: throw Exception("Platform $platform is not supported by ffmpeg")

        val targetFolder = downloadDirectory.resolve("ffbinaries").resolve(version).toFile()

        return listOf(
            httpClient.downloadFileWithProgress(ffmpegUrl.ffmpeg, targetFolder.resolve("ffmpeg")),
            httpClient.downloadFileWithProgress(ffmpegUrl.ffprobe, targetFolder.resolve("ffprobe")))
    }

    private suspend fun getFfmpegRelease(httpClient: HttpClient): FfmpegRelease {
        val releaseResponse: HttpResponse = httpClient.get(ffBinariesUrl)
        return releaseResponse.body()
    }
}
