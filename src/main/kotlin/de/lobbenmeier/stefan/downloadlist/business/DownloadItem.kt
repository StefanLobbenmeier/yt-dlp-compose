package de.lobbenmeier.stefan.downloadlist.business

import de.lobbenmeier.stefan.common.business.YtDlpJson
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DownloadItem(
    val ytDlp: YtDlp,
    val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4"
) {

    val logger = KotlinLogging.logger {}
    val metadata = MutableStateFlow<VideoMetadata?>(null)
    val downloadProgress = MutableStateFlow<VideoDownloadProgress?>(null)
    val targetFile = MutableStateFlow<File?>(null)
    val format = DownloadItemFormat()

    companion object {
        private const val PROGRESS_PREFIX = "[download-progress]"
        private const val PROGRESS_TEMPLATE = "$PROGRESS_PREFIX%(progress)j"
        private const val VIDEO_METADATA_JSON_PREFIX = "[video-metadata-json]"
    }

    fun download(selectedVideoOption: Format?, selectedAudioOption: Format?) {
        CoroutineScope(Dispatchers.IO).launch {
            downloadProgress.emit(DownloadStarted)
            targetFile.emit(null)
            var videoMetadata: VideoMetadata? = null
            try {
                ytDlp.runAsync(
                    true,
                    // Print the whole object again so we get the filename
                    "--print",
                    "$VIDEO_METADATA_JSON_PREFIX%()j",
                    // Required because of the print
                    "--no-simulate",
                    "--no-quiet",
                    *selectFormats(selectedVideoOption, selectedAudioOption),
                    "--progress-template",
                    PROGRESS_TEMPLATE,
                    url,
                ) { log ->
                    when {
                        log.startsWith(PROGRESS_PREFIX) -> {
                            val progressJson = log.removePrefix(PROGRESS_PREFIX)
                            try {
                                val progress =
                                    YtDlpJson.decodeFromString<YtDlpDownloadProgress>(progressJson)
                                downloadProgress.emit(progress)
                            } catch (e: Exception) {
                                logger.warn(e) { "Failed to parse progressJson $progressJson" }
                            }
                        }
                        log.startsWith(VIDEO_METADATA_JSON_PREFIX) -> {
                            val videoMedataJson = log.removePrefix(VIDEO_METADATA_JSON_PREFIX)
                            try {
                                videoMetadata =
                                    YtDlpJson.decodeFromString<VideoMetadata>(videoMedataJson)
                            } catch (e: Exception) {
                                logger.warn(e) { "Failed to parse metadata $videoMetadata" }
                            }
                        }
                        else -> {
                            logger.info { log }
                        }
                    }
                }
                downloadProgress.emit(DownloadCompleted)
                videoMetadata?.filename?.let { targetFile.emit(File(it)) }
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
        val selectedFormats = listOfNotNull(selectedVideoOption, selectedAudioOption).distinct()

        if (selectedFormats.isEmpty()) {
            // Do not download the video but write all related files
            return arrayOf("--skip-download")
        }

        return arrayOf("-f", selectedFormats.joinToString("+") { it.formatId })
    }

    fun gatherMetadata() {
        CoroutineScope(Dispatchers.IO).launch {
            ytDlp.runAsync(
                false,
                "--print",
                "$VIDEO_METADATA_JSON_PREFIX%()j",
                "--flat-playlist",
                url,
            ) { log ->
                when {
                    log.startsWith(VIDEO_METADATA_JSON_PREFIX) -> {
                        val videoMedataJson = log.removePrefix(VIDEO_METADATA_JSON_PREFIX)
                        val videoMetadata =
                            YtDlpJson.decodeFromString<VideoMetadata>(videoMedataJson)
                        metadata.value = videoMetadata

                        val requestedFormats = videoMetadata.requestedFormats
                        if (requestedFormats != null) {
                            requestedFormats.forEach(format::selectFormat)
                        } else {
                            videoMetadata.formats?.let { formats ->
                                // set audio first, so we have a default option
                                val audioFormat = formats.lastOrNull { it.isAudioOnly }
                                audioFormat?.let { selectAudioFormat(it) }

                                val videoFormat = formats.lastOrNull { it.isVideo }
                                videoFormat?.let { selectVideoFormat(it) }
                            }
                        }
                    }
                    else -> {
                        logger.info { log }
                    }
                }
            }
        }
    }

    fun selectVideoFormat(ytDlpFormat: Format?) = format.selectVideoFormat(ytDlpFormat)

    fun selectAudioFormat(ytDlpFormat: Format?) = format.selectAudioFormat(ytDlpFormat)

    val fileSize = format.size
}

class DownloadItemFormat {

    private val selectedVideoFormat = MutableStateFlow<Format?>(null)
    private val selectedAudioFormat = MutableStateFlow<Format?>(null)

    private var defaultAudioFormat: Format? = null

    val video: StateFlow<Format?>
        get() = selectedVideoFormat

    val audio: StateFlow<Format?>
        get() = selectedAudioFormat

    fun selectFormat(ytDlpFormat: Format) {
        if (ytDlpFormat.isVideo) {
            selectVideoFormat(ytDlpFormat)
        }
        if (ytDlpFormat.isAudio) {
            selectAudioFormat(ytDlpFormat)
        }
    }

    fun selectVideoFormat(ytDlpFormat: Format?) {
        selectedVideoFormat.value = ytDlpFormat

        if (
            !ytDlpFormat.isAudio && selectedAudioFormat.value.isVideo && defaultAudioFormat != null
        ) {
            // reset the audio format to default again, to avoid having to download 2 videos
            selectedAudioFormat.value = defaultAudioFormat
        }
    }

    fun selectAudioFormat(ytDlpFormat: Format?) {
        selectedAudioFormat.value = ytDlpFormat

        if (defaultAudioFormat == null && ytDlpFormat.isAudioOnly) {
            defaultAudioFormat = ytDlpFormat
        }
    }

    private val allSelectedFormats
        get() = video.combine(audio) { video, audio -> listOfNotNull(video, audio).distinct() }

    val size =
        allSelectedFormats.map { formats ->
            formats.map { format -> format.size }.reduceOrNull(Size::plus) ?: UnknownSize
        }
}
