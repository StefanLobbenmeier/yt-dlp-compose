package de.lobbenmeier.stefan.ytdlp

import kotlinx.serialization.Serializable

enum class DownloadProgressState {
    Starting,
    Completed,
    Failed,
    InProgress
}

interface DownloadProgress {
    val progress: Float
    val state: DownloadProgressState
}

sealed interface VideoDownloadProgress : DownloadProgress

sealed interface UpdateDownloadProgress : DownloadProgress

class CustomUpdateDownloadProgress(
    override val progress: Float,
    override val state: DownloadProgressState = DownloadProgressState.InProgress
) : UpdateDownloadProgress

sealed class CustomDownloadProgress(
    override val progress: Float,
    override val state: DownloadProgressState
) : VideoDownloadProgress, UpdateDownloadProgress

object DownloadStarted : CustomDownloadProgress(0f, DownloadProgressState.Starting)

object DownloadCompleted : CustomDownloadProgress(1f, DownloadProgressState.Completed)

class DownloadFailed(val e: Exception) : CustomDownloadProgress(1f, DownloadProgressState.Failed)

@Serializable
data class YtDlpDownloadProgress(
    val status: String? = null,
    val downloadedBytes: Long? = null,
    val totalBytes: Long? = null,
    val totalBytesEstimate: Float? = null,
    val tmpfilename: String? = null,
    val filename: String? = null,
    val eta: Int? = null,
    val speed: Float? = null,
    val elapsed: Float? = null,
    val ctxId: String? = null,
) : VideoDownloadProgress {
    override val progress: Float
        get() {
            if (downloadedBytes == null) return 0f

            return when {
                totalBytes != null -> downloadedBytes.toFloat() / totalBytes
                totalBytesEstimate != null -> downloadedBytes.toFloat() / totalBytesEstimate
                else -> 0f
            }
        }

    override val state: DownloadProgressState = DownloadProgressState.InProgress
}
