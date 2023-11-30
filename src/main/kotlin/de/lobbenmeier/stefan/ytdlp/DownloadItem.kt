package de.lobbenmeier.stefan.ytdlp

import de.lobbenmeier.stefan.YtDlpJson
import de.lobbenmeier.stefan.platform.getPlatform
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DownloadItem(
    val ytDlp: YtDlp,
    val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4"
) {

    val metadata = MutableStateFlow<VideoMetadata?>(null)
    val downloadProgress = MutableStateFlow<VideoDownloadProgress?>(null)
    val targetFile = MutableStateFlow<File?>(null)
    val format = DownloadItemFormat()

    companion object {
        private const val PROGRESS_PREFIX = "[download-progress]"
        private const val PROGRESS_TEMPLATE = "$PROGRESS_PREFIX%(progress)j"
    }

    fun download(selectedVideoOption: Format?, selectedAudioOption: Format?) {
        CoroutineScope(Dispatchers.IO).launch {
            downloadProgress.emit(DownloadStarted)
            try {
                ytDlp.runAsync(
                    *selectFormats(selectedVideoOption, selectedAudioOption),
                    "--progress-template",
                    PROGRESS_TEMPLATE,
                    "--write-thumbnail",
                    url) { log ->
                        if (log.startsWith(PROGRESS_PREFIX)) {
                            val progressJson = log.removePrefix(PROGRESS_PREFIX)
                            val progress =
                                YtDlpJson.decodeFromString<YtDlpDownloadProgress>(progressJson)
                            downloadProgress.emit(progress)
                        } else {
                            println(log)
                        }
                    }
                downloadProgress.emit(DownloadCompleted)

                ytDlp.runAsync(
                    *selectFormats(selectedVideoOption, selectedAudioOption),
                    "--print",
                    "filename",
                    url) { log ->
                        targetFile.emit(getPlatform().downloadsFolder.resolve(log).toFile())
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                downloadProgress.emit(DownloadFailed(e))
            }
        }
    }

    private fun selectFormats(
        selectedVideoOption: Format?,
        selectedAudioOption: Format?
    ): Array<String> {
        val selectedFormats = listOfNotNull(selectedVideoOption, selectedAudioOption)

        if (selectedFormats.isEmpty()) {
            return arrayOf()
        }

        return arrayOf("-f", selectedFormats.joinToString("+") { it.formatId })
    }

    fun gatherMetadata() {
        CoroutineScope(Dispatchers.IO).launch {
            ytDlp.runAsync("-J", "--flat-playlist", url) { metadataJson ->
                metadata.value = YtDlpJson.decodeFromString<VideoMetadata>(metadataJson)
            }
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

    val allSelectedFormats
        get() = video.combine(audio) { video, audio -> listOfNotNull(video, audio).distinct() }

    val size =
        allSelectedFormats.map { formats ->
            formats
                .map { format -> format.filesize ?: format.filesizeApprox ?: 0 }
                .fold(0, Long::plus)
        }
}
