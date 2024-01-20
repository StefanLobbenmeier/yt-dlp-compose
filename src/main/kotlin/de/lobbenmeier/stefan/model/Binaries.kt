package de.lobbenmeier.stefan.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.platform.getPlatform
import de.lobbenmeier.stefan.update.UpdateProcess
import de.lobbenmeier.stefan.update.createYtDlpDownloader
import de.lobbenmeier.stefan.update.ffmpeg.FfmpegReleaseDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class Binaries {
    private val downloadDirectory = getPlatform().binariesFolder
    private val ytDlpDownloader = createYtDlpDownloader(downloadDirectory)
    private val ffmpegReleaseDownloader = FfmpegReleaseDownloader(downloadDirectory)

    val downloads = mutableStateListOf<UpdateProcess>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val platform = getPlatform()
            downloads.add(ytDlpDownloader.downloadRelease(platform.ytDlpName.filename))
            downloads.addAll(ffmpegReleaseDownloader.downloadRelease(platform))

            downloads.forEach { it.progress.onEach {} }
        }
    }
}
