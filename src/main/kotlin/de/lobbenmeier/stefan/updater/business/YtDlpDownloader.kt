package de.lobbenmeier.stefan.updater.business

import de.lobbenmeier.stefan.settings.business.YtDlpLocation
import de.lobbenmeier.stefan.updater.business.github.GithubReleaseDownloader
import java.nio.file.Path

fun createYtDlpDownloader(
    downloadDirectory: Path,
    ytDlpSource: YtDlpLocation?
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
