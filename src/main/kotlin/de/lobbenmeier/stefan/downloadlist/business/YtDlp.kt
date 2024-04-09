package de.lobbenmeier.stefan.downloadlist.business

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import de.lobbenmeier.stefan.common.business.getDynamicSemaphoreSingleton
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.updater.business.getPlatform
import de.lobbenmeier.stefan.updater.model.Binaries
import kotlin.io.path.pathString
import kotlinx.coroutines.sync.withPermit

class YtDlp(private val binaries: Binaries, private val settings: Settings) {

    private val semaphore = getDynamicSemaphoreSingleton(settings.maxConcurrentJobs?.toInt() ?: 100)

    fun createDownloadItem(url: String): DownloadItem {
        return DownloadItem(this, url).also { it.gatherMetadata() }
    }

    suspend fun runAsync(
        isDownloadJob: Boolean,
        vararg options: String,
        consumer: suspend (String) -> Unit = { line -> println("process $line") }
    ) {
        val ytDlpBinary = binaries.ytDlp.pathString
        val ffmpegBinary = binaries.ffmpeg.pathString
        val fullOptions =
            arrayOf(
                "--ffmpeg-location",
                ffmpegBinary,
                "-v",
                "--compat-opt",
                "manifest-filesize-approx",
                *settings.toYtDlpConfiguration(),
                *options,
            )

        val command = arrayOf(ytDlpBinary, *fullOptions).joinToString(separator = " ") { "\"$it\"" }

        val res =
            withPermit(isDownloadJob) {
                println("Start process: $command")

                process(
                    ytDlpBinary,
                    *fullOptions,
                    stdout = Redirect.Consume { it.collect(consumer) },
                    stderr = Redirect.Consume { it.collect(consumer) },
                    directory = getPlatform().downloadsFolder.toFile()
                )
            }

        println("Script finished with result=${res.resultCode}")

        if (res.resultCode != 0) {
            throw Exception("yt-dlp indicated error in its response")
        }
    }

    private suspend fun <T> withPermit(
        usePermit: Boolean,
        action: suspend () -> T,
    ): T {
        return if (usePermit) {
            semaphore.withPermit { action() }
        } else {
            action()
        }
    }
}
