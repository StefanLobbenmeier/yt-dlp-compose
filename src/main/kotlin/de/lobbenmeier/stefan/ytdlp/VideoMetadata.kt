import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    val thumbnail: String?,
    val webpageUrl: String?,
)