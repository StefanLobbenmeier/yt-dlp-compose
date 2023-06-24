package de.lobbenmeier.stefan.update.github

import de.lobbenmeier.stefan.GithubJson
import de.lobbenmeier.stefan.update.UpdateProcess
import de.lobbenmeier.stefan.update.downloadFileWithProgress
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import java.nio.file.Path

class GithubReleaseDownloader(
    private val owner: String,
    private val repo: String,
    private val downloadDirectory: Path,
    private val githubApi: String = "https://api.github.com",
) {
    suspend fun downloadRelease(assetName: String): UpdateProcess {
        val httpClient = HttpClient() { install(ContentNegotiation) { json(GithubJson) } }

        val githubRelease = getGithubRelease(httpClient)
        return downloadGithubReleaseToFile(githubRelease, assetName, httpClient)
    }

    private suspend fun downloadGithubReleaseToFile(
        githubRelease: GithubRelease,
        assetName: String,
        httpClient: HttpClient
    ): UpdateProcess {
        val version = githubRelease.tagName
        val asset = githubRelease.assets.first { it.name == assetName }

        val targetFolder = downloadDirectory.resolve(owner).resolve(repo)
        targetFolder.toFile().mkdirs()
        val targetFile = targetFolder.resolve(version).toFile()

        return httpClient.downloadFileWithProgress(asset.downloadUrl, targetFile)
    }

    private suspend fun getGithubRelease(httpClient: HttpClient): GithubRelease {
        val url = "$githubApi/repos/$owner/$repo/releases/latest"
        val releaseResponse: HttpResponse = httpClient.get(url)
        return releaseResponse.body()
    }
}
