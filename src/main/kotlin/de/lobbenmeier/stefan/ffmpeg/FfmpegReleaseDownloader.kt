package de.lobbenmeier.stefan.ffmpeg

import de.lobbenmeier.stefan.GithubJson
import de.lobbenmeier.stefan.platform.Platform
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.io.File
import java.nio.file.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FfmpegReleaseDownloader(
    val downloadDirectory: Path,
    val ffBinariesUrl: String = "https://ffbinaries.com/api/v1/version/latest"
) {
    suspend fun downloadRelease(platform: Platform): File {
        val httpClient = HttpClient() { install(ContentNegotiation) { json(GithubJson) } }

        val ffmpegRelease = getFfmpegRelease(httpClient)
        return downloadFfmpegReleaseToFile(ffmpegRelease, platform, httpClient)
    }

    private suspend fun downloadFfmpegReleaseToFile(
        ffmpegRelease: FfmpegRelease,
        platform: Platform,
        httpClient: HttpClient
    ): File {
        val version = ffmpegRelease.version
        val ffmpegUrl =
            ffmpegRelease.bin[platform.ffmpegPlatform.platformName]
                ?: throw Exception("Platform $platform is not supported by ffmpeg")

        val ffmpeg = httpClient.get(ffmpegUrl.ffmpeg)

        val targetFolder = downloadDirectory.resolve("ffbinaries")
        targetFolder.toFile().mkdirs()
        val targetFile = targetFolder.resolve(version).toFile()

        return withContext(Dispatchers.IO) {
            targetFile.createNewFile()
            ffmpeg.bodyAsChannel().copyTo(targetFile.writeChannel())
            return@withContext targetFile
        }
    }

    private suspend fun getFfmpegRelease(httpClient: HttpClient): FfmpegRelease {
        val releaseResponse: HttpResponse = httpClient.get(ffBinariesUrl)
        return releaseResponse.body()
    }
}
