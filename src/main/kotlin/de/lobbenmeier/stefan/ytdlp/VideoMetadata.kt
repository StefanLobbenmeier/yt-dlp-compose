package de.lobbenmeier.stefan.ytdlp

import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    val duration: Double?,
    val filename: String?,
    val formats: List<Format>?,
    val thumbnail: String?,
    val thumbnails: List<Thumbnail>?,
    val title: String?,
    val webpageUrl: String?,
)

val VideoMetadata.thumbnailWithFallBack
    get() = thumbnail ?: thumbnails?.lastOrNull()?.url

@Serializable
data class Format(
    val formatId: String,
    val filesize: Long?,
    val filesizeApprox: Long?,
    val tbr: Double?,
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
    get() = this?.acodec.isSet
val Format?.isAudioOnly
    get() = isAudio && !isVideo
val Format?.isVideo: Boolean
    get() {
        return this?.vcodec.isSet
    }

val Format.videoDescription: String
    get() {
        var moreDetails = ""
        if (height != null) {
            moreDetails += "${height}p"

            if (fps != null) {
                moreDetails += fps.toInt()
            }
        }

        if (vcodec != null) {
            if (moreDetails != "") moreDetails += " "
            moreDetails += "${vcodec}"
        }

        if (moreDetails != "") {
            moreDetails = "($moreDetails)"
        }

        return listOfNotNull(formatNote, moreDetails).joinToString(" ")
    }

val Format.audioDescription: String
    get() {
        var text = format ?: ""
        if (isVideo) text = "included in Video $text"
        if (acodec != null) {
            text += "(${acodec})"
        }
        return text
    }

val Format.size
    get() =
        when {
            this.filesize != null -> ActualSize(filesize)
            this.filesizeApprox != null -> EstimatedSize(filesizeApprox)
            else -> UnknownSize
        }

@Serializable
data class Thumbnail(
    val height: Int?,
    val id: String?,
    val resolution: String?,
    val url: String?,
    val width: Int?
)
