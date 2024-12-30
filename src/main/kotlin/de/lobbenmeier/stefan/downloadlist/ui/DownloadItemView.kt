package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem

@Composable
fun DownloadItemView(downloadItem: DownloadItem, removeItem: (DownloadItem) -> Unit) {
    return Card {
        Column {
            DownloadItemTopView(downloadItem, removeItem)
            DownloadItemPlaylistEntriesView(downloadItem)
        }
    }
}
