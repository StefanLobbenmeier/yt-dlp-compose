package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.ytdlp.DownloadItem

@Composable
fun DownloadList() {
    val downloadList = List(20) {
        DownloadItem()
    }
    LazyColumn(modifier = Modifier.padding(8.0.dp).fillMaxSize()) {
        downloadList.forEach {
            item { Text(it.url) }
        }
    }
}