package de.lobbenmeier.stefan.updater.business.github

import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import de.lobbenmeier.stefan.updater.business.downloadFile
import de.lobbenmeier.stefan.updater.business.updaterHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File
import java.nio.file.Path

class GithubReleaseDownloader(
    private val owner: String,
    private val repo: String,
    private val downloadDirectory: Path,
    private val githubApi: String = "https://api.github.com",
) {
    private val httpClient = updaterHttpClient

    suspend fun downloadRelease(
        assetName: String,
        onProgress: suspend (UpdateDownloadProgress) -> Unit = {},
    ): File {

        val githubRelease = getLatestGithubRelease()
        return downloadGithubReleaseToFile(githubRelease, assetName, onProgress)
    }

    private suspend fun downloadGithubReleaseToFile(
        githubRelease: GithubRelease,
        assetName: String,
        onProgress: suspend (UpdateDownloadProgress) -> Unit,
    ): File {
        val version = githubRelease.tagName
        val asset = githubRelease.assets.first { it.name == assetName }

        val targetFolder = downloadDirectory.resolve(owner).resolve(repo)
        targetFolder.toFile().mkdirs()
        val targetFile = targetFolder.resolve(version).toFile()

        return httpClient.downloadFile(asset.downloadUrl, targetFile, onProgress = onProgress)
    }

    private suspend fun getLatestGithubRelease(): GithubRelease {
        val url = "$githubApi/repos/$owner/$repo/releases/latest"
        val releaseResponse: HttpResponse = httpClient.get(url)
        return releaseResponse.body()
    }

    suspend fun getGithubReleases(): List<GithubRelease> {
        val url = "$githubApi/repos/$owner/$repo/releases"
        val releaseResponse: HttpResponse = httpClient.get(url)
        return releaseResponse.body()
    }
}
