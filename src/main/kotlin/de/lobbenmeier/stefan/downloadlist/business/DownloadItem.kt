package de.lobbenmeier.stefan.downloadlist.business

import androidx.compose.runtime.toMutableStateList
import de.lobbenmeier.stefan.common.business.YtDlpJson
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.nio.file.Files
import kotlin.io.path.writeText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DownloadItem(val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4") :
    CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val logger = KotlinLogging.logger {}

    val uiKey = "$url ${System.currentTimeMillis()}"
    val _state = MutableStateFlow<DownloadItemState>(DownloadItemState(url = url))
    val state = _state.asStateFlow()

    companion object {
        private const val PROGRESS_PREFIX = "[download-progress]"
        private const val PROGRESS_TEMPLATE = "$PROGRESS_PREFIX%(progress)j"
        private const val VIDEO_METADATA_JSON_PREFIX = "[video-metadata-json]"
    }

    fun download() {
        val previousState = state.value
        val videoMetadata = previousState.metadata?.videoMetadata ?: return
        val format = previousState.downloadItemOptions.format

        if (videoMetadata.type == "playlist") {
            async {
                videoMetadata.entries?.forEachIndexed { i, _ -> asyncDownloadPlaylistEntry(i) }
            }
        } else {
            async {
                val download = DownloadState()
                val downloadingState =
                    previousState.copy(status = DownloadItemStatus.DOWNLOADING, download = download)
                _state.value = downloadingState

                doDownload(
                    *selectFormats(format.video.value, format.audio.value),
                    onLog = { downloadingState.logs.add(it) },
                    onProgress = { download.progress.value = it },
                    onDone = { downloadFile ->
                        _state.value =
                            state.value.copy(
                                status = DownloadItemStatus.DONE,
                                download = download.copy(downloadFile = downloadFile),
                            )
                    },
                )
            }
        }
    }

    fun downloadPlaylistEntry(index: Int) {
        async { asyncDownloadPlaylistEntry(index) }
    }

    private suspend fun DownloadItem.asyncDownloadPlaylistEntry(index: Int) {
        val indexForYtDlp = index + 1

        val playlistItemStates = state.value.playlistItemStates
        val downloadItemState = playlistItemStates.getOrNull(index) ?: return

        val download = DownloadState()
        playlistItemStates[index] =
            downloadItemState.copy(status = DownloadItemStatus.DOWNLOADING, download = download)

        doDownload(
            "--playlist-items",
            "$indexForYtDlp",
            *getYtDlp().initialFormatSelection(),
            onLog = { downloadItemState.logs.add(it) },
            onProgress = { download.progress.value = it },
            onDone = { downloadFile ->
                playlistItemStates[index] =
                    downloadItemState.copy(
                        status = DownloadItemStatus.DONE,
                        download = download.copy(downloadFile = downloadFile),
                    )
            },
        )
    }

    private suspend fun doDownload(
        vararg extraOptions: String,
        onLog: (String) -> Unit,
        onProgress: (VideoDownloadProgress) -> Unit,
        onDone: (File) -> Unit,
    ) {
        var videoMetadata: VideoMetadata? = null
        try {
            getYtDlp().runAsync(
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
                            onProgress(progress)
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
                        onLog(log)
                        logger.info { log }
                    }
                }
            }
            onProgress(DownloadCompleted)
            videoMetadata?.filename?.let { onDone(File(it)) }
        } catch (e: Exception) {
            logger.error(e) { "Failed to download video $url" }
            onProgress(DownloadFailed(e))

            val message = _state.value.logs.lastOrNull { it.startsWith("ERROR:") } ?: e.message

            _state.value =
                _state.value.copy(status = DownloadItemStatus.ERROR, errorMessage = message)
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
        selectedAudioOption: Format?,
    ): Array<String> {
        val selectedFormats = listOfNotNull(selectedVideoOption, selectedAudioOption).distinct()

        if (selectedFormats.isEmpty()) {
            // Do not download the video but write all related files
            return arrayOf("--skip-download")
        }

        if (selectedVideoOption == null && selectedAudioOption != null) {
            // Download only audio, otherwise you might get an mp4 file with no video
            // Watch out for --merge-formats though, which seems to override this
            return arrayOf("-f", selectedAudioOption.formatId, "--extract-audio")
        }

        return arrayOf("-f", selectedFormats.joinToString("+") { it.formatId })
    }

    private fun useCachedMetadata(): Array<String> {
        val metadataFile = state.value.metadata?.metadataFile ?: return arrayOf()
        return arrayOf("--load-info-json", metadataFile.absolutePath)
    }

    fun gatherMetadata() {
        async { doGatherMetadata() }
    }

    suspend fun doGatherMetadata() {
        try {
            val ytDlp = getYtDlp()
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
                        async {
                            val videoMetadata = YtDlpJson.decodeFromString<VideoMetadata>(log)
                            val metadataFile = writeMetadataToFile(log)

                            _state.value =
                                state.value.copy(
                                    status = DownloadItemStatus.READY_FOR_DOWNLOAD,
                                    metadata = Metadata(videoMetadata, metadataFile),
                                    playlistItemStates =
                                        videoMetadata.entries
                                            .orEmpty()
                                            .map { it ->
                                                DownloadItemState(
                                                    url = it.webpageUrl ?: url,
                                                    status = DownloadItemStatus.READY_FOR_DOWNLOAD,
                                                    metadata = Metadata(it, metadataFile),
                                                )
                                            }
                                            .toMutableStateList(),
                                )

                            val format = state.value.downloadItemOptions.format
                            if (ytDlp.shouldSelectFormats()) {
                                videoMetadata.requestedDownloadFormats?.forEach(
                                    format::selectFormat
                                )
                            }
                        }
                    }

                    LogLevel.STDERR -> {
                        logger.info { log }
                        state.value.logs.add(log)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to gather metadata" }
            val message = _state.value.logs.lastOrNull { it.startsWith("ERROR:") } ?: e.message

            _state.value =
                _state.value.copy(status = DownloadItemStatus.ERROR, errorMessage = message)
        }
    }

    private suspend fun writeMetadataToFile(videoMetadataJson: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val tmpFilePath = Files.createTempFile("yt-dlp-compose", "yt-dlp-metadata.json")
                tmpFilePath.writeText(videoMetadataJson)
                val tmpFile = tmpFilePath.toFile()
                logger.info { "Wrote metadata to $tmpFile" }
                tmpFile.deleteOnExit()
                tmpFile
            } catch (e: Exception) {
                logger.warn(e) { "Failed to create metadata file" }
                null
            }
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
