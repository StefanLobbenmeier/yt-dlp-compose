package de.lobbenmeier.stefan.settings.business

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    var app: AppSettings,
    var ytDlp: YtDlpSettings,
) {
    @Serializable
    data class AppSettings(
        var maxConcurrentJobs: Int = 4,
        //        var appearance: Theme,
    )

    @Serializable
    data class YtDlpSettings(
        // Network
        var proxy: String?,
        var limitRate: Int?,

        // Output
        var mergeOutputFormat: String?,
        var remuxFormat: String?,
        var recodeFormat: String?,

        // embed
        var embedSubtitles: Boolean?,
        var embedMetadata: Boolean?,
        var embedThumbnail: Boolean?,
        var embedChapters: Boolean?,

        // files
        var filenameTemplate: String?,
        var saveMetadataToJsonFile: Boolean?,
        var saveThumbnailToFile: Boolean?,
        var keepUnmergedFiles: Boolean?,
    )
}
