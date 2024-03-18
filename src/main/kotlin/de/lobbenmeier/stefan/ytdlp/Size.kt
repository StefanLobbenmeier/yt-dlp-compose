package de.lobbenmeier.stefan.ytdlp

sealed interface Size {
    val size: Long
}

data class ActualSize(override val size: Long) : Size

data class EstimatedSize(override val size: Long) : Size

data object UnknownSize : Size {
    override val size: Long = 0L
}

operator fun Size.plus(other: Size): Size {
    return if (this is ActualSize && other is ActualSize) {
        ActualSize(this.size + other.size)
    } else if (this is UnknownSize && other is UnknownSize) {
        UnknownSize
    } else {
        EstimatedSize(this.size + other.size)
    }
}

fun fileSizeString(size: Size?): String {
    val fileSizeString = fileSizeString(size?.size?.toDouble() ?: 0.0)
    return when (size) {
        is ActualSize -> fileSizeString
        is EstimatedSize -> "~$fileSizeString"
        is UnknownSize,
        null -> "Unknown"
    }
}

fun fileSizeString(bytes: Double) =
    when {
        bytes >= 1 shl 30 -> "%.1f GiB".format(bytes / (1 shl 30))
        bytes >= 1 shl 20 -> "%.1f MiB".format(bytes / (1 shl 20))
        bytes >= 1 shl 10 -> "%.1f KiB".format(bytes / (1 shl 10))
        else -> "$bytes B"
    }
