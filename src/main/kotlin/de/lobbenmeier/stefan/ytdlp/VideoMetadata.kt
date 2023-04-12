package de.lobbenmeier.stefan.ytdlp

import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    val duration: Double?,
    val formats: List<Format>?,
    val thumbnail: String?,
    val thumbnails: List<Thumbnail>?,
    val title: String?,
    val webpageUrl: String?,
) {}

val VideoMetadata.thumbnailWithFallBack
    get() = thumbnail ?: thumbnails?.lastOrNull()?.url

@Serializable
data class Format(
    val formatId: String,
    val filesize: Long?,
    val filesizeApprox: Long?,
    val videoExt: String?,
    val vcodec: String?,
    val audioExt: String?,
    val acodec: String?,
    val format: String?,
    val formatNote: String?,
    val ext: String?,
    val fps: Double?,
    val height: Int?,
    val width: Int?,
) {}

private val String?.isSet
    get() = this != null && this != "none"
val Format?.isAudio
    get() = this?.audioExt.isSet
val Format.isAudioOnly
    get() = this.isAudio && !this.isVideo
val Format.isVideo
    get() = this.videoExt.isSet

@Serializable
data class Thumbnail(
    val height: Int?,
    val id: String?,
    val resolution: String?,
    val url: String?,
    val width: Int?
)
