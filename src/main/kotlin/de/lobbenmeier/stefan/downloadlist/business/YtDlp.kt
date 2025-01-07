package de.lobbenmeier.stefan.downloadlist.business

import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import de.lobbenmeier.stefan.common.business.getDynamicSemaphoreSingleton
import de.lobbenmeier.stefan.settings.business.Settings
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
        consumer: suspend (String, LogLevel) -> Unit = { line, _ -> println("process $line") },
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
                    stdout = Redirect.Consume { it.collect { consumer(it, LogLevel.STDOUT) } },
                    stderr = Redirect.Consume { it.collect { consumer(it, LogLevel.STDERR) } },
                )
            }

        println("Script finished with result=${res.resultCode}")

        if (res.resultCode != 0) {
            throw Exception("yt-dlp indicated error in its response")
        }
    }

    private suspend fun <T> withPermit(usePermit: Boolean, action: suspend () -> T): T {
        return if (usePermit) {
            semaphore.withPermit { action() }
        } else {
            action()
        }
    }

    fun initialFormatSelection(): Array<out String> {
        return when {
            settings.selectAudio && settings.selectVideo -> {
                // Default choice by yt-dlp is to download both audio and video
                arrayOf()
            }
            settings.selectAudio -> {
                arrayOf("--format", "bestaudio/bestaudio*")
            }
            settings.selectVideo -> {
                arrayOf("--format", "bestvideo/bestvideo*")
            }
            else -> {
                // Even with --skip-download the metadata will include requested downloads,
                // so we have to filter them out manually, see shouldSelectFormats
                arrayOf()
            }
        }
    }

    fun shouldSelectFormats(): Boolean = settings.selectAudio || settings.selectVideo
}
