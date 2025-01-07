package de.lobbenmeier.stefan.updater.business

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll

class BinariesUpdaterKtTest : AnnotationSpec() {

    @Test
    fun testDetectOnPath() {
        val testDirectory = tempdir()
        val testYtDlp = testDirectory.resolve("yt-dlp")
        testYtDlp.createNewFile()

        detectOnPath("yt-dlp", testDirectory.absolutePath) shouldContain testYtDlp
    }

    @Test
    fun testDetectOnPathWithMultipleFolders() {
        val testDirectory1 = tempdir()
        val testYtDlp1 = testDirectory1.resolve("yt-dlp")
        testYtDlp1.createNewFile()

        val testDirectory2 = tempdir()
        testDirectory2.resolve("foo").createNewFile()

        val testDirectory3 = tempdir()
        val testYtDlp3 = testDirectory3.resolve("yt-dlp")
        testYtDlp3.createNewFile()

        detectOnPath(
            "yt-dlp",
            listOf(testDirectory1, testDirectory2, testDirectory3)
                .joinToString(platform.pathDelimiter),
        ) shouldContainAll listOf(testYtDlp1, testYtDlp3)
    }
}
