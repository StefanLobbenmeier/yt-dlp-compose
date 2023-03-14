package de.lobbenmeier.stefan.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.ytdlp.DownloadItem

class DownloadQueue {
    fun add(downloadItem: DownloadItem) {
        items.add(downloadItem)
    }

    val items = mutableStateListOf<DownloadItem>()
}