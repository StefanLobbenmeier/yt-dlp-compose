package de.lobbenmeier.stefan.settings.business

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    // App Settings
    val maxConcurrentJobs: UInt?,
    //        val appearance: Theme,

    // Network
    val proxy: String?,
    val rateLimit: UInt?,

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
    val filenameTemplate: String?,
    val saveMetadataToJsonFile: Boolean = false,
    val saveThumbnailToFile: Boolean = false,
    val keepUnmergedFiles: Boolean = false,
)
