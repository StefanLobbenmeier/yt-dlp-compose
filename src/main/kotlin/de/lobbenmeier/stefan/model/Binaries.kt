package de.lobbenmeier.stefan.model

import java.nio.file.Path

data class Binaries(
    val ytDlp: Path,
    val ffmpeg: Path,
    val ffprobe: Path,
)
