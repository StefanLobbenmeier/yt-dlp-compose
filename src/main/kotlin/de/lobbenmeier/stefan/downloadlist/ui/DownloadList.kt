package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.downloadlist.model.DownloadQueue

@Composable
fun DownloadList(downloadQueue: DownloadQueue) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            downloadQueue.items.asReversed(),
            key = { it.uiKey },
            itemContent = { DownloadItemView(it, removeItem = downloadQueue::remove) },
        )
    }
}
