package de.lobbenmeier.stefan.updater.business.github

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.engine.spec.tempdir
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

class GithubReleaseDownloaderTest : AnnotationSpec() {
    private var downloadDirectory = tempdir().toPath()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun downloadRelease() = runTest {
        GithubReleaseDownloader("yt-dlp", "yt-dlp", downloadDirectory)
            .downloadRelease("yt-dlp_macos")
    }
}
