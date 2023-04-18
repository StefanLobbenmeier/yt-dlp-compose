package de.lobbenmeier.stefan.ffmpeg

data class FfmpegRelease(
    val bin: Map<String, FfmpegUrls>,
    val permalink: String,
    val version: String
)

data class FfmpegUrls(
    val ffmpeg: String,
    val ffprobe: String,
)
