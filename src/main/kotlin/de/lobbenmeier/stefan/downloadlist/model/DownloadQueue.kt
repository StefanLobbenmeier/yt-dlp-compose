package de.lobbenmeier.stefan.downloadlist.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import de.lobbenmeier.stefan.downloadlist.business.YtDlp

class DownloadQueue(private val ytDlp: YtDlp) {

    val items = mutableStateListOf<DownloadItem>()

    init {
        add("https://www.youtube.com/watch?v=JKjN5mmnSX0")
    }

    fun add(url: String) {
        val downloadItem = ytDlp.createDownloadItem(url)
        items.add(downloadItem)
    }

    fun remove(downloadItem: DownloadItem) {
        items.remove(downloadItem)
    }
}
