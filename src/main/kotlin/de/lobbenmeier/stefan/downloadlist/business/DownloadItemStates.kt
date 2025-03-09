package de.lobbenmeier.stefan.downloadlist.business

import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow

data class DownloadItemState(
    val url: String,
    val status: DownloadItemStatus = DownloadItemStatus.GATHERING_METADATA,
    val logs: SnapshotStateList<String> = SnapshotStateList(),
    val metadata: Metadata? = null,
    val downloadItemOptions: DownloadItemOptions = DownloadItemOptions(),
    val download: DownloadState? = null,
    val playlistItemStates: SnapshotStateList<DownloadItemState> = SnapshotStateList(),
)

val DownloadItemState.videoMetadata
    get() = this.metadata?.videoMetadata

enum class DownloadItemStatus {
    GATHERING_METADATA,
    READY_FOR_DOWNLOAD,
    DOWNLOADING,
    DONE,
}

data class Metadata(val videoMetadata: VideoMetadata, val metadataFile: File?)

data class DownloadItemOptions(
    val format: DownloadItemFormat = DownloadItemFormat()
    // future: specific options like video trimming (+sponsorblock?), passwords, subtitles,
)

data class DownloadState(
    val progress: MutableStateFlow<VideoDownloadProgress> = MutableStateFlow(DownloadStarted),
    val downloadFile: File? = null,
)
