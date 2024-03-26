package de.lobbenmeier.stefan.settings.business

data class Settings(
    val app: AppSettings,
    val ytDlp: YtDlpSettings,
) {
    data class AppSettings(
        val maxConcurrentJobs: Int = 4,
        //        val appearance: Theme,
    )

    data class YtDlpSettings(
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
}
