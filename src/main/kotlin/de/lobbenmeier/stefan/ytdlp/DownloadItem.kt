package de.lobbenmeier.stefan.ytdlp

import VideoMetadata
import kotlinx.coroutines.flow.MutableStateFlow

class DownloadItem(val ytDlp: YtDlp, val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4") {

    val metadata = MutableStateFlow<VideoMetadata?>(null)

    fun download() {
        ytDlp.run(url)
    }

    fun gatherMetadata() {
        ytDlp.run("--print", url)
        metadata.value = VideoMetadata("thumbnail", "url")
    }
}
