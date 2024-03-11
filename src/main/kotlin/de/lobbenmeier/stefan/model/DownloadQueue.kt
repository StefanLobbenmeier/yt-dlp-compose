package de.lobbenmeier.stefan.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.ytdlp.DownloadItem
import de.lobbenmeier.stefan.ytdlp.YtDlp

class DownloadQueue(val ytDlp: YtDlp) {

    val items = mutableStateListOf<DownloadItem>()

    init {
        add("https://www.youtube.com/watch?v=JKjN5mmnSX0")
    }

    fun add(url: String) {
        val downloadItem = ytDlp.createDownloadItem(url)
        items.add(downloadItem)
    }
}
