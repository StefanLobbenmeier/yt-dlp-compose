package de.lobbenmeier.stefan.ytdlp

import de.lobbenmeier.stefan.YtDlpJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class DownloadItem(
    val ytDlp: YtDlp,
    val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4"
) {

    val selectedVideoFormat = MutableStateFlow<Format?>(null)
    val selectedAudioFormat = MutableStateFlow<Format?>(null)
    val metadata = MutableStateFlow<VideoMetadata?>(null)

    fun download() {
        ytDlp.run(url)
    }

    fun gatherMetadata() {
        CoroutineScope(Dispatchers.IO).launch {
            val metadataJson = ytDlp.runAsync("-J", "--flat-playlist", url)

            metadata.value = YtDlpJson.decodeFromString<VideoMetadata>(metadataJson)
        }
    }

    fun selectFormat(format: Format) {
        if (format.acodec != "none") {
            selectedAudioFormat.value = format
        }
        if (format.vcodec != "none") {
            selectedVideoFormat.value = format
        }
    }

    val fileSize =
        selectedVideoFormat.combine(selectedAudioFormat) { video, audio ->
            var acc = 0
            if (video != null) {
                acc += video.filesize ?: video.filesizeApprox ?: 0
            }
            if (audio != null && audio != video) {
                acc += audio.filesize ?: audio.filesizeApprox ?: 0
            }
            acc
        }
}
