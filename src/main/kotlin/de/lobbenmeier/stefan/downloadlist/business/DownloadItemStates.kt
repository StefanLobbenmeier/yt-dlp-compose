package de.lobbenmeier.stefan.downloadlist.business

import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface SingleOrPlaylistState {
    val url: String
    val logs: SnapshotStateList<String>
}

sealed interface DownloadItemState : SingleOrPlaylistState

sealed interface MetadataAvailable {
    val metadata: VideoMetadata
    val metadataFile: File?
}

sealed interface DownloadProgressAvailable {
    val progress: MutableStateFlow<VideoDownloadProgress>
}

data class GatheringMetadata(
    override val url: String,
    override val logs: SnapshotStateList<String>,
) : DownloadItemState

data class ReadyForDownload(
    override val url: String,
    override val logs: SnapshotStateList<String>,
    override val metadata: VideoMetadata,
    val format: DownloadItemFormat,
    override val metadataFile: File?,
) : DownloadItemState, MetadataAvailable

data class Downloading(
    override val url: String,
    override val logs: SnapshotStateList<String>,
    override val metadata: VideoMetadata,
    override val metadataFile: File?,
    override val progress: MutableStateFlow<VideoDownloadProgress>,
) : DownloadItemState, MetadataAvailable, DownloadProgressAvailable

data class Done(
    override val url: String,
    override val logs: SnapshotStateList<String>,
    override val metadata: VideoMetadata,
    override val metadataFile: File?,
    override val progress: MutableStateFlow<VideoDownloadProgress>,
    val downloadFile: File,
) : DownloadItemState, MetadataAvailable, DownloadProgressAvailable

data class PlaylistReadyForDownload(
    override val url: String,
    override val logs: SnapshotStateList<String>,
    override val metadata: VideoMetadata,
    override val metadataFile: File?,
    val playlistItemStates: SnapshotStateList<DownloadItemState>,
) : SingleOrPlaylistState, MetadataAvailable
