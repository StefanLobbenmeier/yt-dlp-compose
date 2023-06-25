package de.lobbenmeier.stefan.update

import de.lobbenmeier.stefan.update.github.GithubReleaseDownloader
import java.nio.file.Path

fun createYtDlpDownloader(downloadDirectory: Path) =
    GithubReleaseDownloader("yt-dlp", "yt-dlp", downloadDirectory)
