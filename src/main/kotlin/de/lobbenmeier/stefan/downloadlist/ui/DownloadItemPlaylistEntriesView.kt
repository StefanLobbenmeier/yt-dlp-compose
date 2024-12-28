package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import de.lobbenmeier.stefan.common.ui.reloadUiEvery
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import de.lobbenmeier.stefan.downloadlist.business.VideoMetadata
import de.lobbenmeier.stefan.downloadlist.business.entryThumbnail
import kotlin.time.Duration.Companion.seconds

private val entryHeight = 50.dp

@Composable
fun DownloadItemPlaylistEntriesView(downloadItem: DownloadItem) {
    reloadUiEvery(1.seconds).value

    val metadataNonFinal by downloadItem.metadata.collectAsState()
    val metadata = metadataNonFinal

    if (metadata == null || metadata.type != "playlist") {
        return
    }

    if (metadata.entries.isNullOrEmpty()) {
        return Text(text = "Playlist is empty")
    }

    LazyColumn(
        modifier = Modifier.height(entryHeight * minOf(metadata.entries.size, 5)),
        userScrollEnabled = true
    ) {
        items(metadata.entries, itemContent = { entry -> PlaylistEntryView(entry) })
    }
}

@Composable
fun PlaylistEntryView(entry: VideoMetadata) {
    reloadUiEvery(1.seconds).value

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(all = 4.dp).height(entryHeight)
    ) {
        Thumbnail(entry.entryThumbnail)
        Spacer(Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier.weight(1f),
                text = entry.title ?: "No Title",
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(onClick = {}) { Icon(Icons.Default.Add, "Add") }
        IconButton(onClick = {}) { Icon(FeatherIcons.Download, "Download") }
    }
}
