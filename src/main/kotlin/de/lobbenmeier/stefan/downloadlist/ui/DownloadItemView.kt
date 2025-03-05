package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem

@Composable
fun DownloadItemView(downloadItem: DownloadItem, removeItem: (DownloadItem) -> Unit) {
    val state = downloadItem.state.collectAsState().value

    return Card(modifier = Modifier.semantics { contentDescription = "Download Item" }) {
        Column {
            DownloadItemTopView(downloadItem, removeItem, state)
            DownloadItemPlaylistEntriesView(downloadItem, state)
        }
    }
}
