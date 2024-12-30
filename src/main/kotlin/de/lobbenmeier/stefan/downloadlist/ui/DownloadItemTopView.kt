package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
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
import de.lobbenmeier.stefan.downloadlist.business.DownloadCompleted
import de.lobbenmeier.stefan.downloadlist.business.DownloadFailed
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import de.lobbenmeier.stefan.downloadlist.business.DownloadStarted
import de.lobbenmeier.stefan.downloadlist.business.Format
import de.lobbenmeier.stefan.downloadlist.business.VideoDownloadProgress
import de.lobbenmeier.stefan.downloadlist.business.VideoMetadata
import de.lobbenmeier.stefan.downloadlist.business.YtDlp
import de.lobbenmeier.stefan.downloadlist.business.YtDlpDownloadProgress
import de.lobbenmeier.stefan.downloadlist.business.audioDescription
import de.lobbenmeier.stefan.downloadlist.business.fileSizeString
import de.lobbenmeier.stefan.downloadlist.business.isAudio
import de.lobbenmeier.stefan.downloadlist.business.isAudioOnly
import de.lobbenmeier.stefan.downloadlist.business.isVideo
import de.lobbenmeier.stefan.downloadlist.business.thumbnailWithFallBack
import de.lobbenmeier.stefan.downloadlist.business.videoDescription
import de.lobbenmeier.stefan.settings.business.createEmptySettings
import de.lobbenmeier.stefan.updater.model.homeBrewBinaries
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun DownloadItemTopView(downloadItem: DownloadItem, removeItem: (DownloadItem) -> Unit) {
    val metadata by downloadItem.metadata.collectAsState()
    val thumbnail = metadata?.thumbnailWithFallBack

    val selectedVideoOption by downloadItem.format.video.collectAsState()
    val selectedAudioOption by downloadItem.format.audio.collectAsState(initial = null)

    Row(Modifier.requiredHeight(135.dp)) {
        Thumbnail(thumbnail)
        Column(
            Modifier.weight(1f).padding(20.dp, 15.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(metadata?.title ?: downloadItem.url, fontSize = 1.1.em)

            FormatSelectorOrDownloadProgress(
                downloadItem,
                metadata,
                selectedVideoOption,
                selectedAudioOption
            )
            InformationRow(metadata, downloadItem)
        }
        Divider(Modifier.fillMaxHeight().width(1.dp))
        Column {
            IconButton(
                onClick = { downloadItem.download(selectedVideoOption, selectedAudioOption) }
            ) {
                Icon(FeatherIcons.Download, "Download")
            }
            val file = downloadItem.getTargetFile().collectAsState().value
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

@Composable
private fun InformationRow(metadata: VideoMetadata?, downloadItem: DownloadItem) {
    if (metadata == null) {
        return Text("Downloading metadata...")
    }

    val downloadProgress by downloadItem.getProgress().collectAsState()
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
            val fileSize by downloadItem.fileSize.collectAsState(null)
            Text(
                "Size: ",
                fontWeight = FontWeight.Bold,
            )
            Text(fileSizeString(fileSize))
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
    val downloadProgress by downloadItem.getProgress().collectAsState()

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

    if (metadata == null) {
        return LinearProgressIndicator(Modifier.fillMaxWidth())
    }

    val formats =
        metadata.formats?.asReversed()
            ?: return Text("No formats available, most likely because this is a playlist")

    val videoFormats = listOf(null) + formats.filter { it.isVideo }
    val audioFormats =
        listOf(null) +
            formats.filter {
                it.isAudioOnly || (selectedVideoOption.isAudio && it == selectedVideoOption)
            }

    Row {
        DropdownMenu(
            videoFormats,
            selectedOption = selectedVideoOption,
            selectionChanged = { downloadItem.selectVideoFormat(it) },
            modifier = Modifier.weight(1f),
            optionFormatter = { it?.videoDescription ?: "(No Video)" }
        )
        DropdownMenu(
            audioFormats,
            selectedOption = selectedAudioOption,
            selectionChanged = { downloadItem.selectAudioFormat(it) },
            modifier = Modifier.weight(1f),
            optionFormatter = { it?.audioDescription ?: "(No Audio)" }
        )
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

@Preview
@Composable
private fun DownloadItemPreview() {

    val downloadItem =
        YtDlp(homeBrewBinaries, createEmptySettings())
            .createDownloadItem("https://www.youtube.com/watch?v=JKjN5mmnSX0")
    DownloadItemView(downloadItem) {}
}
