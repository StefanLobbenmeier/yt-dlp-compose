package de.lobbenmeier.stefan.ytdlp

class DownloadItem(val url: String = "https://www.youtube.com/watch?v=CBB75zjxTR4") {
    fun getOptions(): Array<String> {
        return arrayOf(url)
    }

}
