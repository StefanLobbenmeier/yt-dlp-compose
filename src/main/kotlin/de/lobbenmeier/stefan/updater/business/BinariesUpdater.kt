package de.lobbenmeier.stefan.updater.business

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import de.lobbenmeier.stefan.downloadlist.business.DownloadStarted
import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import de.lobbenmeier.stefan.settings.business.BinariesSettings
import de.lobbenmeier.stefan.settings.business.FfmpegLocation
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.business.YtDlpLocation
import de.lobbenmeier.stefan.updater.business.ffmpeg.FfmpegReleaseDownloader
import de.lobbenmeier.stefan.updater.model.Binaries
import de.lobbenmeier.stefan.updater.model.BinariesProgress
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
        downloadOrFindBinaries()
    }

    private fun downloadOrFindBinaries() {

        CoroutineScope(Dispatchers.IO).launch {
            val ytDlpFuture =
                if (
                    binariesSettings.ytDlpSource != YtDlpLocation.DISK ||
                        binariesSettings.ytDlpPath == null
                ) {
                    val ytDlpProcess = downloadYtDlp()
                    async {
                        ytDlpDownloader.downloadRelease(
                            platform.ytDlpName.filename,
                            withProgress(ytDlpProcess)
                        )
                    }
                } else {
                    async { findBinary(binariesSettings.ytDlpPath, platform.ytDlpName.filename) }
                }

            val ffmpegFuture =
                if (
                    binariesSettings.ffmpegSource != FfmpegLocation.DISK ||
                        binariesSettings.ffmpegPath == null
                ) {
                    async {
                        val (ffmpegProcess, ffprobeProcess) = downloadFfmpeg()
                        val binaries =
                            ffmpegReleaseDownloader.downloadRelease(
                                platform,
                                withProgress(ffmpegProcess),
                                withProgress(ffprobeProcess)
                            )
                        binaries.first()
                    }
                } else {
                    async {
                        // yt-dlp will resolve for us
                        File(binariesSettings.ffmpegPath)
                    }
                }

            val ytDlp = ytDlpFuture.await()
            val ffmpeg = ffmpegFuture.await()

            binaries.value =
                Binaries(
                    ytDlp.toPath(),
                    ffmpeg.toPath(),
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

    private fun findBinary(path: String, binary: String): File {
        val ytDlpPathAsFile = File(path)
        if (ytDlpPathAsFile.isFile()) {
            return ytDlpPathAsFile
        }

        return ytDlpPathAsFile.resolve(binary)
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
