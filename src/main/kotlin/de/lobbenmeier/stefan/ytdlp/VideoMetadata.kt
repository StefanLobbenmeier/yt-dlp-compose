import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    val thumbnail: String? = null,
    val webpageUrl: String? = null,
)