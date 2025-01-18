package de.lobbenmeier.stefan.updater.business

import de.lobbenmeier.stefan.settings.business.YtDlpLocation
import de.lobbenmeier.stefan.updater.business.github.GithubReleaseDownloader
import java.nio.file.Path

fun createYtDlpDownloader(
    downloadDirectory: Path,
    ytDlpSource: YtDlpLocation?,
): GithubReleaseDownloader {
    val repo =
        when (ytDlpSource) {
            YtDlpLocation.DISK,
            YtDlpLocation.STABLE,
            null -> "yt-dlp"
            YtDlpLocation.NIGHTLY -> "yt-dlp-nightly-builds"
            YtDlpLocation.MASTER -> "yt-dlp-master-builds"
        }
    return GithubReleaseDownloader("yt-dlp", repo, downloadDirectory)
}

fun getYtDlpChannelFolders(downloadDirectory: Path) =
    listOf("yt-dlp", "yt-dlp-nightly-builds", "yt-dlp-master-builds").map {
        downloadDirectory.resolve("yt-dlp").resolve(it).toFile()
    }
