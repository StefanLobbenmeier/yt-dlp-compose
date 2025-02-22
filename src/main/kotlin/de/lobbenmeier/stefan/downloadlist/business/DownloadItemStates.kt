package de.lobbenmeier.stefan.downloadlist.business

import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.File

sealed interface DownloadItemState {
    val url: String
    val logs: SnapshotStateList<String>
}

interface MetadataAvailable {
    val metadata: VideoMetadata
    val format: DownloadItemFormat
    val metadataFile: File?
}

data class GatheringMetadata(
    override val url: String,
    override val logs: SnapshotStateList<String>,
) : DownloadItemState

data class ReadyForDownload(
    override val url: String,
    override val logs: SnapshotStateList<String>,
    override val metadata: VideoMetadata,
    override val format: DownloadItemFormat,
    override val metadataFile: File?,
) : DownloadItemState, MetadataAvailable

data class Downloading(
    override val url: String,
    override val logs: SnapshotStateList<String>,
    val progress: SnapshotStateList<String>,
) : DownloadItemState

data class Done(
    override val url: String,
    override val logs: SnapshotStateList<String>,
    val downloadFile: File,
) : DownloadItemState
