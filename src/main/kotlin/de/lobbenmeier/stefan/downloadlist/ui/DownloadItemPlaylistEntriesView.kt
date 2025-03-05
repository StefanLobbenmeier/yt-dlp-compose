package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import de.lobbenmeier.stefan.downloadlist.business.DownloadItemState
import de.lobbenmeier.stefan.downloadlist.business.Downloading
import de.lobbenmeier.stefan.downloadlist.business.MetadataAvailable
import de.lobbenmeier.stefan.downloadlist.business.PlaylistReadyForDownload
import de.lobbenmeier.stefan.downloadlist.business.entryThumbnail

private val entryHeight = 50.dp

@Composable
fun DownloadItemPlaylistEntriesView(downloadItem: DownloadItem, state: PlaylistReadyForDownload) {
    val metadata = state.metadata

    if (metadata.entries.isNullOrEmpty()) {
        return Text(text = "Playlist is empty")
    }

    LazyColumn(
        modifier = Modifier.height(entryHeight * minOf(metadata.entries.size, 5)),
        userScrollEnabled = true,
    ) {
        itemsIndexed(
            state.playlistItemStates,
            itemContent = { index, playlistItemState ->
                PlaylistEntryView(downloadItem, index, playlistItemState)
            },
        )
    }
}

@Composable
fun PlaylistEntryView(
    downloadItem: DownloadItem,
    index: Int,
    playlistItemState: DownloadItemState,
) {
    if (playlistItemState !is MetadataAvailable) return
    val entry = playlistItemState.metadata

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(all = 4.dp).height(entryHeight),
    ) {
        Thumbnail(entry.entryThumbnail)
        Spacer(Modifier.width(4.dp))

        Column(
            modifier = Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Text(text = entry.title ?: "No Title", fontWeight = FontWeight.Bold, maxLines = 1)

            if (playlistItemState is Downloading) {
                val progress = playlistItemState.progress.collectAsState().value
                DownloadProgressIndicator(progress, modifier = Modifier.height(5.dp))
            }
        }

        IconButton(onClick = { downloadItem.downloadPlaylistEntry(index) }) {
            Icon(FeatherIcons.Download, "Download")
        }
        val targetFile = downloadItem.getTargetFile(index).collectAsState().value
        if (targetFile != null) {
            BrowseFileButton(targetFile)
        }
    }
}
