package de.lobbenmeier.stefan.downloadlist.business

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    val id: String?,
    val duration: Double?,
    val filename: String?,
    val formats: List<Format>?,
    val requestedDownloads: List<Format>?,
    val thumbnail: String?,
    val thumbnails: List<Thumbnail>?,
    val title: String?,
    val webpageUrl: String?,

    // playlist and playlist items only
    @SerialName("_type") val type: String?,
    val entries: List<VideoMetadata>?,
)

val VideoMetadata.thumbnailWithFallBack
    get() = thumbnail ?: thumbnails?.lastOrNull()?.url

val VideoMetadata.entryThumbnail
    get() = thumbnails?.firstOrNull()?.url ?: thumbnail

val VideoMetadata.requestedDownloadFormats: List<Format>?
    get() = requestedDownloads?.firstOrNull()?.requestedFormats ?: requestedDownloads

@Serializable
data class Format(
    val requestedFormats: List<Format>?,
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

// arte audio has an audioExt, but acoded is "none" because it is unknown
val Format?.isAudio
    get() = this?.audioExt.isSet || this?.acodec.isSet
val Format?.isAudioOnly
    get() = isAudio && !isVideo
val Format?.isVideo: Boolean
    get() {
        return this?.videoExt.isSet || this?.vcodec.isSet
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
            moreDetails += vcodec
        }

        if (moreDetails != "") {
            moreDetails = "($moreDetails)"
        }

        return listOfNotNull(formatNote, moreDetails).joinToString(" ")
    }

val Format.audioDescription: String
    get() {
        return listOfNotNull(
                if (isVideo) "from video" else format ?: formatNote,
                acodec?.let { "($acodec)" },
            )
            .joinToString(" ")
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
    val width: Int?,
)
