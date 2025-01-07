package de.lobbenmeier.stefan.settings.business

import de.lobbenmeier.stefan.updater.business.platform
import kotlin.io.path.absolutePathString
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    // App Settings
    val maxConcurrentJobs: UInt?,

    // Binaries Settings
    val ytDlpSource: YtDlpLocation?,
    val ytDlpPath: String?,
    val ffmpegSource: FfmpegLocation?,
    val ffmpegPath: String?,
    //        val appearance: Theme,

    // Network & Authentication
    val proxy: String?,
    val concurrentFragments: UInt?,
    val rateLimit: UInt?,
    val header: String?,
    val cookiesFromBrowser: String?,
    val cookiesFile: String?,

    // Format
    val selectVideo: Boolean = true,
    val selectAudio: Boolean = true,
    val preferFreeFormats: Boolean = false,
    val sortFormats: String?,

    // Output
    val mergeOutputFormat: String?,
    val remuxFormat: String?,
    val recodeFormat: String?,
    val audioFormat: String?,

    // embed
    val embedSubtitles: Boolean = false,
    val embedMetadata: Boolean = false,
    val embedThumbnail: Boolean = false,
    val embedChapters: Boolean = false,

    // files
    val downloadFolder: String = platform.downloadsFolder.absolutePathString(),
    val filenameTemplate: String?,
    val saveMetadataToJsonFile: Boolean = false,
    val saveThumbnailToFile: Boolean = false,
    val keepUnmergedFiles: Boolean = false,
)

data class BinariesSettings(
    val ytDlpSource: YtDlpLocation?,
    val ytDlpPath: String?,
    val ffmpegSource: FfmpegLocation?,
    val ffmpegPath: String?,
)

val Settings.binariesSettings
    get() =
        BinariesSettings(
            ytDlpSource = ytDlpSource,
            ytDlpPath = ytDlpPath,
            ffmpegSource = ffmpegSource,
            ffmpegPath = ffmpegPath
        )
