package de.lobbenmeier.stefan.updater.business

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.github.pgreze.process.Redirect
import com.github.pgreze.process.process
import de.lobbenmeier.stefan.downloadlist.business.DownloadStarted
import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.updater.business.ffmpeg.FfmpegReleaseDownloader
import de.lobbenmeier.stefan.updater.model.Binaries
import de.lobbenmeier.stefan.updater.model.UpdateProcess
import java.nio.file.Path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun detectBinaries(settings: Settings, changeSettings: (Settings) -> Unit) {
    if (settings.ytDlpSource == null) {}
}

fun detectLocalYtDlp(): Path? {
    val paths = listOf("")

    return detectLocalYtDlp()
}

suspend fun detectOnPath(binaryName: String): List<String> {
    return withContext(Dispatchers.IO) {
        buildList<String> {
            val process =
                process(
                    "/bin/bash",
                    "-c",
                    "which $binaryName",
                    stdout = Redirect.Consume { it.collect(::add) },
                )

            println("detectOnPath $this")
        }
    }
}

suspend fun getBashPath(): List<String> {
    return withContext(Dispatchers.IO) {
        buildList<String> {
            val process =
                process(
                    "/bin/bash",
                    "-c",
                    "echo \$PATH",
                    stdout = Redirect.Consume { it.collect(::add) },
                )

            println("getBashPath $this")
        }
    }
}

class BinariesUpdater {
    private val downloadDirectory = platform.binariesFolder
    private val ytDlpDownloader = createYtDlpDownloader(downloadDirectory)
    private val ffmpegReleaseDownloader = FfmpegReleaseDownloader(downloadDirectory)

    val downloads: SnapshotStateList<UpdateProcess>
    val binaries = MutableStateFlow<Binaries?>(null)

    init {
        val ytDlpProcess = updateProcess(platform.ytDlpName.filename)
        val ffmpegProcess = updateProcess("ffmpeg")
        val ffprobeProcess = updateProcess("ffprobe")
        downloads =
            mutableStateListOf(
                ytDlpProcess,
                ffmpegProcess,
                ffprobeProcess,
            )

        CoroutineScope(Dispatchers.IO).launch {
            val ytDlpFuture = async {
                ytDlpDownloader.downloadRelease(
                    platform.ytDlpName.filename,
                    withProgress(ytDlpProcess)
                )
            }
            val ffmpegFuture = async {
                ffmpegReleaseDownloader.downloadRelease(
                    platform,
                    withProgress(ffmpegProcess),
                    withProgress(ffprobeProcess)
                )
            }

            val ytDlp = ytDlpFuture.await()
            val ffmpeg = ffmpegFuture.await()

            binaries.value =
                Binaries(
                    ytDlp.toPath(),
                    ffmpeg.first().toPath(),
                    ffmpeg[1].toPath(),
                )
        }
    }

    private fun updateProcess(
        name: String,
    ): UpdateProcess {
        val progressFlow = MutableStateFlow<UpdateDownloadProgress>(DownloadStarted)
        val updateProcess = UpdateProcess(name, progressFlow)
        return updateProcess
    }

    private fun withProgress(
        updateProcess: UpdateProcess
    ): suspend (UpdateDownloadProgress) -> Unit {
        return { updateProcess.progress.emit(it) }
    }
}
