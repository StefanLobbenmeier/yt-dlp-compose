package de.lobbenmeier.stefan.ytdlp

import kotlinx.serialization.Serializable

sealed interface DownloadProgress {
    val progress: Float
}

sealed class CustomDownloadProgress(override val progress: Float) : DownloadProgress

object DownloadStarted : CustomDownloadProgress(0f)

object DownloadCompleted : CustomDownloadProgress(1f)

class DownloadFailed(val e: Exception) : CustomDownloadProgress(1f)

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
) : DownloadProgress {
    override val progress: Float
        get() {
            if (downloadedBytes == null) return 0f

            return when {
                totalBytes != null -> downloadedBytes.toFloat() / totalBytes
                totalBytesEstimate != null -> downloadedBytes.toFloat() / totalBytesEstimate
                else -> 0f
            }
        }
}
