package de.lobbenmeier.stefan.downloadlist.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import kotlinx.coroutines.cancel

class DownloadQueue {

    val items = mutableStateListOf<DownloadItem>()

    fun add(downloadItem: DownloadItem) {
        items.add(downloadItem)
    }

    fun remove(downloadItem: DownloadItem) {
        downloadItem.cancel()
        // TODO the cancellation does not work yet, so leave the item in the list so I can see it
        // items.remove(downloadItem)
    }

    fun clear() {
        items.clear()
    }

    fun downloadAll() {
        for (item in items) {
            if (item.getTargetFile().value == null) {
                item.download()
            }
        }
    }
}
