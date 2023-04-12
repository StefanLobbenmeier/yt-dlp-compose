package de.lobbenmeier.stefan.ytdlp

import kotlinx.serialization.Serializable

@Serializable
data class DownloadProgress(
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
)

val DownloadProgress.progress: Float
    get() {
        return when (downloadedBytes) {
            null -> 0f
            else ->
                when {
                    totalBytes != null -> downloadedBytes.toFloat() / totalBytes
                    totalBytesEstimate != null -> downloadedBytes.toFloat() / totalBytesEstimate
                    else -> 0f
                }
        }
    }
