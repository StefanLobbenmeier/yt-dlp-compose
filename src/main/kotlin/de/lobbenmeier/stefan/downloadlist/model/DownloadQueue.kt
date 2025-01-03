package de.lobbenmeier.stefan.downloadlist.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem

class DownloadQueue {

    val items = mutableStateListOf<DownloadItem>()

    fun add(downloadItem: DownloadItem) {
        items.add(downloadItem)
    }

    fun remove(downloadItem: DownloadItem) {
        items.remove(downloadItem)
    }

    fun clear() {
        items.clear()
    }
}
