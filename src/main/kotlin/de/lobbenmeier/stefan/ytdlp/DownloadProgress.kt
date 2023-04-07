package de.lobbenmeier.stefan.ytdlp

import kotlinx.serialization.Serializable

@Serializable
data class DownloadProgress(
    val status: String?,
    val downloadedBytes: String?,
    val totalBytes: String?,
    val tmpfilename: String?,
    val filename: String?,
    val eta: String?,
    val speed: String?,
    val elapsed: String?,
    val ctxId: String?,
)
