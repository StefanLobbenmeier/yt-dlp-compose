package de.lobbenmeier.stefan.updater.business

import de.lobbenmeier.stefan.updater.business.github.GithubReleaseDownloader
import java.nio.file.Path

fun createYtDlpDownloader(downloadDirectory: Path) =
    GithubReleaseDownloader("yt-dlp", "yt-dlp-nightly-builds", downloadDirectory)
