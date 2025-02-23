package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem

@Composable
fun DownloadItemView(downloadItem: DownloadItem, removeItem: (DownloadItem) -> Unit) {
    return Card(modifier = Modifier.semantics { contentDescription = "Download Item" }) {
        Column {
            DownloadItemTopView(downloadItem, removeItem)
            DownloadItemPlaylistEntriesView(downloadItem)
        }
    }
}
