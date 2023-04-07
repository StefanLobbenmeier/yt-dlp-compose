package de.lobbenmeier.stefan.ytdlp

import kotlinx.serialization.Serializable

@Serializable
data class DownloadProgress(
    val status: String?,
    val downloadedBytes: Long?,
    val totalBytes: Long?,
    val tmpfilename: String?,
    val filename: String?,
    val eta: Int?,
    val speed: Float?,
    val elapsed: Float?,
    val ctxId: String?,
)
