import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    val duration: Double?,
    val formats: List<Format>?,
    val thumbnail: String?,
    val title: String?,
    val webpageUrl: String?,
) {}

@Serializable
data class Format(
    val formatId: String?,
    val filesize: Int?,
    val filesizeApprox: Int?,
    val vcodec: String?,
    val acodec: String?,
    val format: String?,
    val formatNote: String?,
    val ext: String?,
    val fps: Double?,
    val height: Int?,
    val width: Int?,
) {}
