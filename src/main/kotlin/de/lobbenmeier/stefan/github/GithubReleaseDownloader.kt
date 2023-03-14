package de.lobbenmeier.stefan.github

import de.lobbenmeier.stefan.GithubJson
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

class GithubReleaseDownloader(
    private val owner: String,
    private val repo: String,
    private val downloadDirectory: Path,
    private val githubApi: String = "https://api.github.com",
) {
    suspend fun downloadRelease(assetName: String): File {
        val httpClient = HttpClient() { install(ContentNegotiation) { json(GithubJson) } }

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

        return withContext(Dispatchers.IO) {
            targetFile.createNewFile()
            assetResponse.bodyAsChannel().copyTo(targetFile.writeChannel())
            return@withContext targetFile
        }
    }

    private suspend fun getGithubRelease(httpClient: HttpClient): GithubRelease {
        val url = "$githubApi/repos/$owner/$repo/releases/latest"
        val releaseResponse: HttpResponse = httpClient.get(url)
        return releaseResponse.body()
    }
}
