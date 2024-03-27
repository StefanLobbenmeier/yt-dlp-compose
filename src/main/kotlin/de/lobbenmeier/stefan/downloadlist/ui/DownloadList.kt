package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.downloadlist.model.DownloadQueue

@Composable
fun DownloadList(downloadQueue: DownloadQueue) {
    LazyColumn(modifier = Modifier.padding(8.0.dp).fillMaxSize()) {
        items(
            downloadQueue.items.asReversed(),
            itemContent = { DownloadItemView(it, removeItem = downloadQueue::remove) })
    }
}
