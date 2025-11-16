package de.lobbenmeier.stefan.updater.business

import de.lobbenmeier.stefan.updater.business.github.GithubReleaseDownloader
import java.nio.file.Path

fun createDenoDownloader(downloadDirectory: Path): GithubReleaseDownloader {
    return GithubReleaseDownloader("denoland", "deno", downloadDirectory)
}

fun getDenoChannelFolders(downloadDirectory: Path) =
    listOf(downloadDirectory.resolve("deno").resolve("denoland").toFile())
