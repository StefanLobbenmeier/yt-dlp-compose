package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.common.ui.reloadUiEvery
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import de.lobbenmeier.stefan.downloadlist.business.VideoMetadata
import de.lobbenmeier.stefan.downloadlist.business.entryThumbnail
import kotlin.time.Duration.Companion.seconds

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

    Column { metadata.entries.map { entry -> PlaylistEntryView(entry) } }
}

@Composable
fun PlaylistEntryView(entry: VideoMetadata) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(50.dp).padding(all = 8.dp)
    ) {
        Thumbnail(entry.entryThumbnail)
        Text(entry.title ?: "No Title", fontWeight = FontWeight.Bold)
    }
}
