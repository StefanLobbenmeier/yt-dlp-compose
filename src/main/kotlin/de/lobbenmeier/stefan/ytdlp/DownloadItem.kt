package de.lobbenmeier.stefan.ytdlp

import de.lobbenmeier.stefan.YtDlpJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class DownloadItem(
    val ytDlp: YtDlp,
    val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4"
) {

    val metadata = MutableStateFlow<VideoMetadata?>(null)
    val format = DownloadItemFormat()

    fun download() {
        CoroutineScope(Dispatchers.IO).launch { ytDlp.run(*selectFormats(), url) }
    }

    private suspend fun selectFormats(): Array<String> {
        val selectedFormats = format.allSelectedFormats.firstOrNull() ?: listOf()

        if (selectedFormats.isEmpty()) {
            return arrayOf()
        }

        return arrayOf("-f", selectedFormats.joinToString("+"))
    }

    fun gatherMetadata() {
        CoroutineScope(Dispatchers.IO).launch {
            val metadataJson = ytDlp.runAsync("-J", "--flat-playlist", url)

            metadata.value = YtDlpJson.decodeFromString<VideoMetadata>(metadataJson)
        }
    }

    fun selectFormat(ytDlpFormat: Format) {
        format.selectFormat(ytDlpFormat)
    }

    val fileSize = format.size
}

class DownloadItemFormat {

    private val selectedVideoFormat = MutableStateFlow<Format?>(null)
    private val selectedAudioFormat = MutableStateFlow<Format?>(null)

    val video
        get() = selectedVideoFormat
    val audio =
        selectedVideoFormat.combine(selectedAudioFormat) { video, audio ->
            if (video.isAudio) video else audio
        }

    fun selectFormat(ytDlpFormat: Format) {
        if (ytDlpFormat.isVideo) {
            selectedVideoFormat.value = ytDlpFormat
        } else if (ytDlpFormat.isAudio) {
            selectedAudioFormat.value = ytDlpFormat
        }
    }

    val allSelectedFormats =
        video.combine(audio) { video, audio -> listOfNotNull(video, audio).distinct() }

    val size =
        allSelectedFormats.map { formats ->
            formats
                .map { format -> format.filesize ?: format.filesizeApprox ?: 0 }
                .fold(0, Long::plus)
        }
}
