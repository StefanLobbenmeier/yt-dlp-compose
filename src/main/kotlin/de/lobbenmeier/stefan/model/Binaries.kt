package de.lobbenmeier.stefan.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.platform.getPlatform
import de.lobbenmeier.stefan.update.UpdateProcess
import de.lobbenmeier.stefan.update.createYtDlpDownloader
import de.lobbenmeier.stefan.update.ffmpeg.FfmpegReleaseDownloader
import java.nio.file.Path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Binaries {
    private val downloadDirectory = Path.of("C:\\Users\\Stefan\\Desktop\\yt-dlp-compose\\binaries")
    private val ytDlpDownloader = createYtDlpDownloader(downloadDirectory)
    private val ffmpegReleaseDownloader = FfmpegReleaseDownloader(downloadDirectory)

    val downloads = mutableStateListOf<UpdateProcess>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val platform = getPlatform()
            downloads.add(ytDlpDownloader.downloadRelease(platform.ytDlpName.filename))
            downloads.addAll(ffmpegReleaseDownloader.downloadRelease(platform))
        }
    }
}
