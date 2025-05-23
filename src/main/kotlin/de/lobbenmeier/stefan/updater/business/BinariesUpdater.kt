package de.lobbenmeier.stefan.updater.business

import de.lobbenmeier.stefan.downloadlist.business.DownloadCompleted
import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import de.lobbenmeier.stefan.settings.business.BinariesSettings
import de.lobbenmeier.stefan.settings.business.FfmpegLocation
import de.lobbenmeier.stefan.settings.business.YtDlpLocation
import de.lobbenmeier.stefan.updater.business.ffmpeg.FfmpegReleaseDownloader
import de.lobbenmeier.stefan.updater.business.ffmpeg.getFfmpegChannelFolders
import de.lobbenmeier.stefan.updater.model.Binaries
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BinariesUpdater(private val binariesSettings: BinariesSettings) {
    private val downloadDirectory = platform.binariesFolder

    val binaries = MutableStateFlow<Binaries?>(null)
    private val logger = KotlinLogging.logger {}

    fun downloadOrFindBinaries(
        ytDlpProgress: (UpdateDownloadProgress) -> Unit,
        ffmpegProgress: (UpdateDownloadProgress) -> Unit,
        ffprobeProgress: (UpdateDownloadProgress) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val ytDlpFuture =
                if (
                    binariesSettings.ytDlpSource != YtDlpLocation.DISK ||
                        binariesSettings.ytDlpPath == null
                ) {
                    async {
                        createYtDlpDownloader(downloadDirectory, binariesSettings.ytDlpSource)
                            .downloadRelease(platform.ytDlpName.filename, ytDlpProgress)
                    }
                } else {
                    async {
                        findBinary(binariesSettings.ytDlpPath, platform.ytDlpName.filename).also {
                            ytDlpProgress(DownloadCompleted)
                        }
                    }
                }

            val ffmpegFuture =
                if (
                    binariesSettings.ffmpegSource != FfmpegLocation.DISK ||
                        binariesSettings.ffmpegPath == null
                ) {
                    async {
                        val binaries =
                            FfmpegReleaseDownloader(downloadDirectory)
                                .downloadRelease(platform, ffmpegProgress, ffprobeProgress)
                        binaries.first()
                    }
                } else {
                    async {
                        ffmpegProgress(DownloadCompleted)
                        ffprobeProgress(DownloadCompleted)
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

    private fun findBinary(path: String, binary: String): File {
        val ytDlpPathAsFile = File(path)
        if (ytDlpPathAsFile.isFile()) {
            return ytDlpPathAsFile
        }

        return ytDlpPathAsFile.resolve(binary)
    }

    private fun cleanupOldReleases(ytDlp: File, ffmpeg: File) {
        val ytDlpChannelFolders = getYtDlpChannelFolders(platform.binariesFolder)

        deleteBinariesExceptFor(ytDlpChannelFolders, ytDlp)

        val ffmpegChannelFolders = getFfmpegChannelFolders(platform.binariesFolder)
        deleteBinariesExceptFor(ffmpegChannelFolders, ffmpeg)
    }

    private fun deleteBinariesExceptFor(channelFolders: List<File>, except: File) {
        for (channelFolder in channelFolders) {
            if (!channelFolder.exists()) {
                logger.info { "Channel $channelFolder did not exist yet, nothing to clean up" }
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
