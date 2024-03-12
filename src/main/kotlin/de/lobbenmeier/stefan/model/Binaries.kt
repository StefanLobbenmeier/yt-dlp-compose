package de.lobbenmeier.stefan.model

import java.nio.file.Path

data class Binaries(
    val ytDlp: Path,
    val ffmpeg: Path,
    val ffprobe: Path,
)

val homeBrewBinaries =
    Binaries(
        Path.of("/opt/homebrew/bin/yt-dlp"),
        Path.of("/opt/homebrew/bin/ffmpeg"),
        Path.of("/opt/homebrew/bin/ffprobe"),
    )
