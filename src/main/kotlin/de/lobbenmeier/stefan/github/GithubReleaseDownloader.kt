package de.lobbenmeier.stefan.github

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path


class GithubReleaseDownloader(
    private val owner: String,
    private val repo: String,
    private val downloadDirectory : Path,
    private val githubApi: String = "https://api.github.com",
) {
    suspend fun downloadRelease(assetName: String): File {
        val httpClient = HttpClient() {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val githubRelease = getGithubRelease(httpClient)
        return downloadGithubReleaseToFile(githubRelease, assetName, httpClient)
    }

    private suspend fun downloadGithubReleaseToFile(
        githubRelease: GithubRelease,
        assetName: String,
        httpClient: HttpClient
    ): File {
        val version = githubRelease.tagName
        val asset = githubRelease.assets.first { it.name == assetName }

        val assetResponse = httpClient.get(asset.downloadUrl)

        val targetFolder = downloadDirectory.resolve(owner).resolve(repo)
        targetFolder.toFile().mkdirs()
        val targetFile = targetFolder.resolve(version).toFile()
        targetFile.createNewFile()

        assetResponse.bodyAsChannel().copyTo(targetFile.writeChannel())

        return targetFile
    }

    private suspend fun getGithubRelease(httpClient: HttpClient): GithubRelease {
        val url = "$githubApi/repos/$owner/$repo/releases/latest"
        val releaseResponse: HttpResponse = httpClient.get(url)
        return releaseResponse.body()
    }
}