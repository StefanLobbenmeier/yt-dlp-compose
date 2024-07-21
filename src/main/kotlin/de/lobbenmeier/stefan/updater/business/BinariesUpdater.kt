package de.lobbenmeier.stefan.updater.business

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import de.lobbenmeier.stefan.downloadlist.business.DownloadStarted
import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import de.lobbenmeier.stefan.settings.business.BinariesSettings
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.business.YtDlpLocation
import de.lobbenmeier.stefan.updater.business.ffmpeg.FfmpegReleaseDownloader
import de.lobbenmeier.stefan.updater.model.Binaries
import de.lobbenmeier.stefan.updater.model.BinariesProgress
import de.lobbenmeier.stefan.updater.model.LocalBinaryProgress
import de.lobbenmeier.stefan.updater.model.RemoteBinaryProgress
import java.io.File
import java.nio.file.Path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun detectBinaries(settings: Settings, changeSettings: (Settings) -> Unit) {
    if (settings.ytDlpSource == null) {}
}

fun detectLocalYtDlp(): Path? {
    val paths = listOf("")

    return detectLocalYtDlp()
}

fun detectOnPath(binaryName: String, pathVariable: String = System.getenv("PATH")): List<File> {
    val systemPaths = pathVariable.split(platform.pathDelimiter).map(Path::of)

    return (systemPaths + platform.extraPaths)
        .map { it.resolve(binaryName).toFile() }
        .filter(File::isFile)
}

class BinariesUpdater(
    private val binariesSettings: BinariesSettings,
) {
    private val downloadDirectory = platform.binariesFolder
    private val ytDlpDownloader = createYtDlpDownloader(downloadDirectory)
    private val ffmpegReleaseDownloader = FfmpegReleaseDownloader(downloadDirectory)

    val progress: SnapshotStateList<BinariesProgress> = mutableStateListOf()
    val binaries = MutableStateFlow<Binaries?>(null)

    init {
        progress.add(checkYtDlp())
        downloadBinaries()
    }

    private fun checkYtDlp(): BinariesProgress {
        if (binariesSettings.ytDlpSource == YtDlpLocation.DISK) {
            if (binariesSettings.ytDlpPath != null) {
                return LocalBinaryProgress("yt-dlp", "found on disk")
            }

            val detectOnPath = detectOnPath("yt-dlp")

            if (detectOnPath.isNotEmpty()) {
                return LocalBinaryProgress("yt-dlp", "found on path")
            }
        }

        return downloadYtDlp()
    }

    private fun downloadBinaries() {
        val ytDlpProcess = downloadYtDlp()
        val (ffmpegProcess, ffprobeProcess) = downloadFfmpeg()
        //        progress =
        //            mutableStateListOf(
        //                ytDlpProcess,
        //                ffmpegProcess,
        //                ffprobeProcess,
        //            )

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

    private fun downloadFfmpeg(): Pair<RemoteBinaryProgress, RemoteBinaryProgress> {
        val ffmpegProcess = updateProcess("ffmpeg")
        val ffprobeProcess = updateProcess("ffprobe")
        return Pair(ffmpegProcess, ffprobeProcess)
    }

    private fun downloadYtDlp(): RemoteBinaryProgress {
        return updateProcess(platform.ytDlpName.filename)
    }

    private fun updateProcess(
        name: String,
    ): RemoteBinaryProgress {
        val progressFlow = MutableStateFlow<UpdateDownloadProgress>(DownloadStarted)
        val updateProcess = RemoteBinaryProgress(name, progressFlow)
        return updateProcess
    }

    private fun withProgress(
        updateProcess: RemoteBinaryProgress
    ): suspend (UpdateDownloadProgress) -> Unit {
        return { updateProcess.progress.emit(it) }
    }
}
