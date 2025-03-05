package de.lobbenmeier.stefan.downloadlist.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import de.lobbenmeier.stefan.downloadlist.business.DownloadItemStatus
import kotlinx.coroutines.cancel

class DownloadQueue {

    val items = mutableStateListOf<DownloadItem>()

    fun add(downloadItem: DownloadItem) {
        items.add(downloadItem)
    }

    fun remove(downloadItem: DownloadItem) {
        downloadItem.cancel()
        items.remove(downloadItem)
    }

    fun clear() {
        items.clear()
    }

    fun downloadAll() {
        for (item in items) {
            if (item.state.value.status == DownloadItemStatus.READY_FOR_DOWNLOAD) {
                item.download()
            }
        }
    }
}
