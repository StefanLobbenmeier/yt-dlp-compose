import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    val duration: Int? = null,
    val thumbnail: String? = null,
    val title: String? = null,
    val webpageUrl: String? = null,
) {}
