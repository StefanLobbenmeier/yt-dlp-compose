package de.lobbenmeier.stefan.ytdlp

class YtDlpOptions {
    fun asArray(): Array<String> {
        return arrayOf(url)
    }

    val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4"
}
