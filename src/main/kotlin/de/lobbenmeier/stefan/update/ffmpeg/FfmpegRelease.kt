package de.lobbenmeier.stefan.update.ffmpeg

data class FfmpegRelease(
    val bin: Map<String, FfmpegUrls>,
    val permalink: String,
    val version: String
)

data class FfmpegUrls(
    val ffmpeg: String,
    val ffprobe: String,
)
