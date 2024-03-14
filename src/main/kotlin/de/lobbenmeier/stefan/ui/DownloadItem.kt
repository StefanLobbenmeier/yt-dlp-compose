package de.lobbenmeier.stefan.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import compose.icons.feathericons.XCircle
import de.lobbenmeier.stefan.model.homeBrewBinaries
import de.lobbenmeier.stefan.ytdlp.DownloadCompleted
import de.lobbenmeier.stefan.ytdlp.DownloadFailed
import de.lobbenmeier.stefan.ytdlp.DownloadItem
import de.lobbenmeier.stefan.ytdlp.DownloadStarted
import de.lobbenmeier.stefan.ytdlp.Format
import de.lobbenmeier.stefan.ytdlp.VideoDownloadProgress
import de.lobbenmeier.stefan.ytdlp.VideoMetadata
import de.lobbenmeier.stefan.ytdlp.YtDlp
import de.lobbenmeier.stefan.ytdlp.YtDlpDownloadProgress
import de.lobbenmeier.stefan.ytdlp.audioDescription
import de.lobbenmeier.stefan.ytdlp.isAudioOnly
import de.lobbenmeier.stefan.ytdlp.isVideo
import de.lobbenmeier.stefan.ytdlp.thumbnailWithFallBack
import de.lobbenmeier.stefan.ytdlp.videoDescription
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun DownloadItemView(downloadItem: DownloadItem, removeItem: (DownloadItem) -> Unit) {
    val metadata by downloadItem.metadata.collectAsState()
    val thumbnail = metadata?.thumbnailWithFallBack

    val selectedVideoOption by downloadItem.format.video.collectAsState()
    val selectedAudioOption by downloadItem.format.audio.collectAsState(initial = null)

    Card {
        Row(Modifier.requiredHeight(135.dp)) {
            Thumbnail(thumbnail)
            Column(
                Modifier.weight(1f).padding(20.dp, 15.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween) {
                    Text(metadata?.title ?: downloadItem.url, fontSize = 1.1.em)

                    FormatSelectorOrDownloadProgress(
                        downloadItem, metadata, selectedVideoOption, selectedAudioOption)
                    InformationRow(metadata, downloadItem)
                }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column {
                IconButton(
                    onClick = { downloadItem.download(selectedVideoOption, selectedAudioOption) }) {
                        Icon(FeatherIcons.Download, "Download")
                    }
                val file = downloadItem.targetFile.collectAsState().value
                if (file == null) {
                    IconButton(onClick = { removeItem(downloadItem) }) {
                        Icon(FeatherIcons.XCircle, "Delete")
                    }
                } else {
                    OpenFileButton(file)
                    BrowseFileButton(file)
                }
            }
        }
    }
}

@Composable
private fun InformationRow(metadata: VideoMetadata?, downloadItem: DownloadItem) {
    if (metadata == null) {
        return Text("Downloading metadata...")
    }

    val downloadProgress by downloadItem.downloadProgress.collectAsState()
    val finalDownloadProgress = downloadProgress

    if (finalDownloadProgress == null) {
        VideoInformation(metadata, downloadItem)
    } else {
        DownloadInformation(finalDownloadProgress)
    }
}

@Composable
private fun VideoInformation(metadata: VideoMetadata, downloadItem: DownloadItem) {
    Row {
        Row(modifier = Modifier.weight(1f)) {
            Text("Duration: ", fontWeight = FontWeight.Bold)
            Text(durationString(metadata.duration))
        }
        Row(modifier = Modifier.weight(1f)) {
            val fileSize by downloadItem.fileSize.collectAsState(0)
            Text(
                "Size: ",
                fontWeight = FontWeight.Bold,
            )
            Text(fileSizeString(fileSize.toDouble()))
        }
    }
}

@Composable
private fun DownloadInformation(downloadProgress: VideoDownloadProgress) {
    when (downloadProgress) {
        is YtDlpDownloadProgress ->
            Row {
                Row(modifier = Modifier.weight(1f)) {
                    Text("ETA: ", fontWeight = FontWeight.Bold)
                    Text(durationString(downloadProgress.eta))
                }
                Row(modifier = Modifier.weight(1f)) {
                    Text(
                        "Speed: ",
                        fontWeight = FontWeight.Bold,
                    )
                    val speed = downloadProgress.speed?.toDouble() ?: 0.0
                    Text(fileSizeString(speed) + "/s")
                }
            }
        is DownloadStarted -> {
            Text("Starting Download...")
        }
        is DownloadCompleted -> {
            Text("Completed Download.")
        }
        is DownloadFailed -> {
            Text("Failed to download: " + downloadProgress.e)
        }
    }
}

@Composable
private fun FormatSelectorOrDownloadProgress(
    downloadItem: DownloadItem,
    metadata: VideoMetadata?,
    selectedVideoOption: Format?,
    selectedAudioOption: Format?
) {
    val downloadProgress by downloadItem.downloadProgress.collectAsState(null)

    val finalDownloadProgress = downloadProgress
    if (finalDownloadProgress == null)
        FormatSelector(downloadItem, metadata, selectedVideoOption, selectedAudioOption)
    else DownloadProgressIndicator(finalDownloadProgress)
}

@Composable
private fun FormatSelector(
    downloadItem: DownloadItem,
    metadata: VideoMetadata?,
    selectedVideoOption: Format?,
    selectedAudioOption: Format?
) {
    val formats = metadata?.formats?.asReversed()

    if (formats != null) {
        val videoFormats = formats.filter { it.isVideo }
        val audioFormats = formats.filter { it.isAudioOnly }

        Row {
            DropdownMenu(
                videoFormats,
                selectedOption = selectedVideoOption,
                selectionChanged = { downloadItem.selectFormat(it) },
                modifier = Modifier.weight(1f),
                optionBuilder = { Text(it.videoDescription) })
            DropdownMenu(
                audioFormats,
                selectedOption = selectedAudioOption,
                selectionChanged = { downloadItem.selectFormat(it) },
                modifier = Modifier.weight(1f),
                optionBuilder = { Text(it.audioDescription) })
        }
    } else {
        LinearProgressIndicator(Modifier.fillMaxWidth())
    }
}

private fun durationString(i: Double?): String {
    return durationString(i?.roundToInt())
}

private fun durationString(i: Int?): String {
    if (i == null) {
        return ""
    }
    val duration = i.seconds
    return duration.toString()
}

fun fileSizeString(bytes: Double) =
    when {
        bytes >= 1 shl 30 -> "%.1f GiB".format(bytes / (1 shl 30))
        bytes >= 1 shl 20 -> "%.1f MiB".format(bytes / (1 shl 20))
        bytes >= 1 shl 10 -> "%.1f KiB".format(bytes / (1 shl 10))
        else -> "$bytes B"
    }

@Preview
@Composable
private fun DownloadItemPreview() {

    val downloadItem =
        YtDlp(homeBrewBinaries).createDownloadItem("https://www.youtube.com/watch?v=JKjN5mmnSX0")
    DownloadItemView(downloadItem) {}
}
