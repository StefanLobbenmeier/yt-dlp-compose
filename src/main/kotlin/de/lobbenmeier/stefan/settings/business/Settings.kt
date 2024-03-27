package de.lobbenmeier.stefan.settings.business

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    // App Settings
    val maxConcurrentJobs: Int = 4,
    //        val appearance: Theme,

    // Network
    val proxy: String?,
    val limitRate: Int?,

    // Output
    val mergeOutputFormat: String?,
    val remuxFormat: String?,
    val recodeFormat: String?,

    // embed
    val embedSubtitles: Boolean?,
    val embedMetadata: Boolean?,
    val embedThumbnail: Boolean?,
    val embedChapters: Boolean?,

    // files
    val filenameTemplate: String?,
    val saveMetadataToJsonFile: Boolean?,
    val saveThumbnailToFile: Boolean?,
    val keepUnmergedFiles: Boolean?,
)
