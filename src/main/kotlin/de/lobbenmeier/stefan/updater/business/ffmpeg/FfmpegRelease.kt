package de.lobbenmeier.stefan.updater.business.ffmpeg

import kotlinx.serialization.Serializable

@Serializable
data class FfmpegRelease(
    val bin: Map<String, FfmpegUrls>,
    val permalink: String,
    val version: String
)

@Serializable
data class FfmpegUrls(
    val ffmpeg: String,
    val ffprobe: String,
)
