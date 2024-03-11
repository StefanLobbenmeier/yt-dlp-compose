package de.lobbenmeier.stefan.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import de.lobbenmeier.stefan.platform.getPlatform
import de.lobbenmeier.stefan.update.UpdateProcess
import de.lobbenmeier.stefan.update.createYtDlpDownloader
import de.lobbenmeier.stefan.update.ffmpeg.FfmpegReleaseDownloader
import de.lobbenmeier.stefan.ytdlp.DownloadStarted
import de.lobbenmeier.stefan.ytdlp.UpdateDownloadProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BinariesUpdater {
    private val downloadDirectory = getPlatform().binariesFolder
    private val ytDlpDownloader = createYtDlpDownloader(downloadDirectory)
    private val ffmpegReleaseDownloader = FfmpegReleaseDownloader(downloadDirectory)

    val downloads: SnapshotStateList<UpdateProcess>
    val binaries = MutableStateFlow<Binaries?>(null)

    init {
        val platform = getPlatform()
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
                    platform.ytDlpName.filename, withProgress(ytDlpProcess))
            }
            val ffmpegFuture = async {
                ffmpegReleaseDownloader.downloadRelease(
                    platform, withProgress(ffmpegProcess), withProgress(ffprobeProcess))
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
