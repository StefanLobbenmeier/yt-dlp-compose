package de.lobbenmeier.stefan.updater.business

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import de.lobbenmeier.stefan.downloadlist.business.DownloadStarted
import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import de.lobbenmeier.stefan.settings.business.BinariesSettings
import de.lobbenmeier.stefan.settings.business.FfmpegLocation
import de.lobbenmeier.stefan.settings.business.YtDlpLocation
import de.lobbenmeier.stefan.updater.business.ffmpeg.FfmpegReleaseDownloader
import de.lobbenmeier.stefan.updater.business.ffmpeg.getFfmpegChannelFolders
import de.lobbenmeier.stefan.updater.model.Binaries
import de.lobbenmeier.stefan.updater.model.BinariesProgress
import de.lobbenmeier.stefan.updater.model.LocalBinaryProgress
import de.lobbenmeier.stefan.updater.model.RemoteBinaryProgress
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BinariesUpdater(private val binariesSettings: BinariesSettings) {
    private val downloadDirectory = platform.binariesFolder

    val progress: SnapshotStateList<BinariesProgress> = mutableStateListOf()
    val binaries = MutableStateFlow<Binaries?>(null)
    val logger = KotlinLogging.logger {}

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
                    progress.add(ytDlpProcess)
                    async {
                        createYtDlpDownloader(downloadDirectory, binariesSettings.ytDlpSource)
                            .downloadRelease(
                                platform.ytDlpName.filename,
                                withProgress(ytDlpProcess),
                            )
                    }
                } else {
                    async {
                        findBinary(binariesSettings.ytDlpPath, platform.ytDlpName.filename).also {
                            progress.add(LocalBinaryProgress("yt-dlp", it))
                        }
                    }
                }

            val ffmpegFuture =
                if (
                    binariesSettings.ffmpegSource != FfmpegLocation.DISK ||
                        binariesSettings.ffmpegPath == null
                ) {
                    async {
                        val (ffmpegProcess, ffprobeProcess) = downloadFfmpeg()
                        progress.add(ffmpegProcess)
                        progress.add(ffprobeProcess)
                        val binaries =
                            FfmpegReleaseDownloader(downloadDirectory)
                                .downloadRelease(
                                    platform,
                                    withProgress(ffmpegProcess),
                                    withProgress(ffprobeProcess),
                                )
                        binaries.first()
                    }
                } else {
                    progress.add(LocalBinaryProgress("ffmpeg", File(binariesSettings.ffmpegPath)))
                    async {
                        // yt-dlp will resolve for us
                        File(binariesSettings.ffmpegPath)
                    }
                }

            val ytDlp = ytDlpFuture.await()
            val ffmpeg = ffmpegFuture.await()

            binaries.value = Binaries(ytDlp.toPath(), ffmpeg.toPath())

            cleanupOldReleases(ytDlp, ffmpeg)
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

    private fun updateProcess(name: String): RemoteBinaryProgress {
        val progressFlow = MutableStateFlow<UpdateDownloadProgress>(DownloadStarted)
        val updateProcess = RemoteBinaryProgress(name, progressFlow)
        return updateProcess
    }

    private fun withProgress(
        updateProcess: RemoteBinaryProgress
    ): suspend (UpdateDownloadProgress) -> Unit {
        return { updateProcess.progress.emit(it) }
    }

    private fun cleanupOldReleases(ytDlp: File, ffmpeg: File) {
        val ytDlpChannelFolders = getYtDlpChannelFolders(platform.binariesFolder)

        deleteBinariesExceptFor(ytDlpChannelFolders, ytDlp)

        val ffmpegChannelFolders = getFfmpegChannelFolders(platform.binariesFolder)
        deleteBinariesExceptFor(ffmpegChannelFolders, ytDlp)
    }

    private fun deleteBinariesExceptFor(channelFolders: List<File>, except: File) {
        for (channelFolder in channelFolders) {
            if (!channelFolder.exists()) {
                logger.info { "Channel $channelFolder did not exist yet, nothign to clean up" }
                continue
            }

            val binaryFolders = channelFolder.listFiles()
            val numberOfBinaries = binaryFolders?.size ?: 0
            if (binaryFolders == null || numberOfBinaries <= 1) {
                logger.info {
                    "There are $numberOfBinaries of in $channelFolder, leaving that channel untouched"
                }
                continue
            }

            binaryFolders
                .filterNot { except.absolutePath.startsWith(it.absolutePath) }
                .forEach {
                    val deleteSuccess = it.deleteRecursively()
                    if (!deleteSuccess) {
                        logger.warn { "Failed to delete $it" }
                    } else {
                        logger.info { "Deleted version that was no longer needed: $it" }
                    }
                }
        }
    }
}
