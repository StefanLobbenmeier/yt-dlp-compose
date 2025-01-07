package de.lobbenmeier.stefan.downloadlist.business

import de.lobbenmeier.stefan.common.business.YtDlpJson
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.nio.file.Files
import kotlin.io.path.writeText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadItem(
    val ytDlp: YtDlp,
    val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4"
) {

    val key = "$url ${System.currentTimeMillis()}"
    val metadata = MutableStateFlow<VideoMetadata?>(null)
    val format = DownloadItemFormat()

    private val logger = KotlinLogging.logger {}
    private val metadataFile = MutableStateFlow<File?>(null)
    private val targetFile = mutableMapOf<Int, MutableStateFlow<File?>>()
    private val downloadProgress = mutableMapOf<Int, MutableStateFlow<VideoDownloadProgress?>>()

    companion object {
        private const val PROGRESS_PREFIX = "[download-progress]"
        private const val PROGRESS_TEMPLATE = "$PROGRESS_PREFIX%(progress)j"
        private const val VIDEO_METADATA_JSON_PREFIX = "[video-metadata-json]"
    }

    fun download() {
        val videoMetadata = metadata.value
        if (videoMetadata?.type == "playlist") {

            CoroutineScope(Dispatchers.IO).launch {
                videoMetadata.entries?.forEachIndexed { i, _ -> asyncDownloadPlaylistEntry(i) }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                doDownload(
                    *selectFormats(format.video.value, format.audio.value),
                    progressFlow = getProgress(),
                    targetFile = getTargetFile(),
                )
            }
        }
    }

    fun downloadPlaylistEntry(index: Int) {
        CoroutineScope(Dispatchers.IO).launch { asyncDownloadPlaylistEntry(index) }
    }

    private suspend fun DownloadItem.asyncDownloadPlaylistEntry(index: Int) {
        val indexForYtDlp = index + 1
        doDownload(
            "--playlist-items",
            "$indexForYtDlp",
            progressFlow = getProgress(index),
            targetFile = getTargetFile(index),
        )
    }

    private suspend fun doDownload(
        vararg extraOptions: String,
        progressFlow: MutableStateFlow<VideoDownloadProgress?>,
        targetFile: MutableStateFlow<File?>,
    ) {
        if (progressFlow.value != null) {
            // prevent starting download twice - todo recover after cancellation
            return
        }

        progressFlow.emit(DownloadStarted)
        targetFile.emit(null)
        var videoMetadata: VideoMetadata? = null
        try {
            ytDlp.runAsync(
                true,
                // Print the whole object again so we get the filename
                options = downloadOptions(*extraOptions),
            ) { log, _ ->
                when {
                    log.startsWith(PROGRESS_PREFIX) -> {
                        val progressJson = log.removePrefix(PROGRESS_PREFIX)
                        try {
                            val progress =
                                YtDlpJson.decodeFromString<YtDlpDownloadProgress>(progressJson)
                            progressFlow.emit(progress)
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
            progressFlow.emit(DownloadCompleted)
            videoMetadata?.filename?.let { targetFile.emit(File(it)) }
        } catch (e: Exception) {
            e.printStackTrace()
            progressFlow.emit(DownloadFailed(e))
        }
    }

    private fun downloadOptions(vararg extraOptions: String = arrayOf()) =
        arrayOf(
            "--print",
            "$VIDEO_METADATA_JSON_PREFIX%()j",
            // Required because of the print
            "--no-simulate",
            "--no-quiet",
            *useCachedMetadata(),

            // --no-clean-info-json allows you to reuse json for playlists
            "--no-clean-info-json",
            "--progress-template",
            PROGRESS_TEMPLATE,
            *extraOptions,
            url,
        )

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

    private fun useCachedMetadata(): Array<String> {
        return metadataFile.value?.let { arrayOf("--load-info-json", it.absolutePath) }
            ?: emptyArray<String>()
    }

    fun getProgress(index: Int? = null): MutableStateFlow<VideoDownloadProgress?> {
        val key = index ?: -1
        return downloadProgress.computeIfAbsent(key) { MutableStateFlow(null) }
    }

    fun getTargetFile(index: Int? = null): MutableStateFlow<File?> {
        val key = index ?: -1
        return targetFile.computeIfAbsent(key) { MutableStateFlow(null) }
    }

    fun gatherMetadata() {
        CoroutineScope(Dispatchers.IO).launch {
            ytDlp.runAsync(
                false,
                "--dump-single-json",
                "--no-clean-info-json",
                "--flat-playlist",
                *ytDlp.initialFormatSelection(),
                url,
            ) { log, logLevel ->
                when (logLevel) {
                    LogLevel.STDOUT -> {
                        val videoMetadata = YtDlpJson.decodeFromString<VideoMetadata>(log)
                        metadata.value = videoMetadata
                        async { writeMetadataToFile(log) }

                        videoMetadata.requestedDownloadFormats?.forEach(format::selectFormat)
                    }
                    LogLevel.STDERR -> {
                        logger.info { log }
                    }
                }
            }
        }
    }

    fun selectVideoFormat(ytDlpFormat: Format?) = format.selectVideoFormat(ytDlpFormat)

    fun selectAudioFormat(ytDlpFormat: Format?) = format.selectAudioFormat(ytDlpFormat)

    val fileSize = format.size

    private suspend fun writeMetadataToFile(videoMetadataJson: String) {
        withContext(Dispatchers.IO) {
            val tmpFilePath = Files.createTempFile("yt-dlp-compose", "yt-dlp-metadata.json")
            tmpFilePath.writeText(videoMetadataJson)
            val tmpFile = tmpFilePath.toFile()
            logger.info { "Wrote metadata to $tmpFile" }
            tmpFile.deleteOnExit()
            metadataFile.value = tmpFile
        }
    }
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
