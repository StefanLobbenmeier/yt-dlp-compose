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
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import compose.icons.feathericons.Info
import compose.icons.feathericons.XCircle
import de.lobbenmeier.stefan.common.ui.SmallIconButton
import de.lobbenmeier.stefan.downloadlist.business.DownloadCompleted
import de.lobbenmeier.stefan.downloadlist.business.DownloadFailed
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import de.lobbenmeier.stefan.downloadlist.business.DownloadItemState
import de.lobbenmeier.stefan.downloadlist.business.DownloadItemStatus
import de.lobbenmeier.stefan.downloadlist.business.DownloadStarted
import de.lobbenmeier.stefan.downloadlist.business.VideoDownloadProgress
import de.lobbenmeier.stefan.downloadlist.business.YtDlp
import de.lobbenmeier.stefan.downloadlist.business.YtDlpDownloadProgress
import de.lobbenmeier.stefan.downloadlist.business.audioDescription
import de.lobbenmeier.stefan.downloadlist.business.fileSizeString
import de.lobbenmeier.stefan.downloadlist.business.isAudio
import de.lobbenmeier.stefan.downloadlist.business.isAudioOnly
import de.lobbenmeier.stefan.downloadlist.business.isVideo
import de.lobbenmeier.stefan.downloadlist.business.thumbnailWithFallBack
import de.lobbenmeier.stefan.downloadlist.business.videoDescription
import de.lobbenmeier.stefan.downloadlist.business.videoMetadata
import de.lobbenmeier.stefan.settings.business.createEmptySettings
import de.lobbenmeier.stefan.updater.model.homeBrewBinaries
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun DownloadItemTopView(
    downloadItem: DownloadItem,
    removeItem: (DownloadItem) -> Unit,
    state: DownloadItemState,
) {
    val metadata = state.videoMetadata
    val thumbnail = metadata?.thumbnailWithFallBack

    Row(Modifier.requiredHeight(140.dp)) {
        Thumbnail(thumbnail)
        Column(
            Modifier.weight(1f).padding(10.dp, 15.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                metadata?.title ?: downloadItem.url,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
            )

            FormatSelectorOrDownloadProgress(state)
            InformationRow(state)
        }
        Divider(Modifier.fillMaxHeight().width(1.dp))
        Column(
            modifier = Modifier.padding(8.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            var informationDialogOpen by remember { mutableStateOf(false) }

            if (informationDialogOpen) {
                InformationDialog(state, onClose = { informationDialogOpen = false })
            }

            SmallIconButton(onClick = { informationDialogOpen = true }) {
                Icon(FeatherIcons.Info, "Information and Logs")
            }

            if (state.status != DownloadItemStatus.DONE) {
                SmallIconButton(onClick = { downloadItem.download() }) {
                    Icon(FeatherIcons.Download, "Download")
                }
            }

            SmallIconButton(onClick = { removeItem(downloadItem) }) {
                Icon(FeatherIcons.XCircle, "Remove Item")
            }

            if (state.status == DownloadItemStatus.DONE) {
                val file = state.download?.downloadFile
                OpenFileButton(file)
                BrowseFileButton(file)
            }
        }
    }
}

@Composable
private fun InformationRow(state: DownloadItemState) {
    when (state.status) {
        DownloadItemStatus.GATHERING_METADATA -> {
            Text("Downloading metadata...")
        }

        DownloadItemStatus.READY_FOR_DOWNLOAD -> {
            VideoInformation(state)
        }

        DownloadItemStatus.DOWNLOADING,
        DownloadItemStatus.DONE -> {
            val progress = state.download?.progress?.collectAsState()?.value ?: DownloadStarted
            DownloadInformation(progress)
        }

        DownloadItemStatus.ERROR -> {
            ViewLogsButton(state)
        }
    }
}

@Composable
private fun VideoInformation(state: DownloadItemState) {
    Row {
        Row(modifier = Modifier.weight(1f)) {
            Text("Duration: ", fontWeight = FontWeight.Bold)
            Text(durationString(state.videoMetadata?.duration))
        }
        Row(modifier = Modifier.weight(1f)) {
            val fileSize by state.downloadItemOptions.format.size.collectAsState(null)
            Text("Size: ", fontWeight = FontWeight.Bold)
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
                    Text("Speed: ", fontWeight = FontWeight.Bold)
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
fun ViewLogsButton(state: DownloadItemState) {
    var logsDialogopen by remember { mutableStateOf(false) }

    if (logsDialogopen) {
        LogsDialog(state, onClose = { logsDialogopen = false })
    }

    Button(onClick = { logsDialogopen = true }) { Text("Show logs") }
}

@Composable
private fun FormatSelectorOrDownloadProgress(state: DownloadItemState) {
    when (state.status) {
        DownloadItemStatus.GATHERING_METADATA -> {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        DownloadItemStatus.READY_FOR_DOWNLOAD -> {
            FormatSelector(state)
        }

        DownloadItemStatus.DOWNLOADING,
        DownloadItemStatus.DONE -> {
            val progress = state.download?.progress?.collectAsState()?.value ?: DownloadStarted
            DownloadProgressIndicator(progress)
        }

        DownloadItemStatus.ERROR -> {
            ErrorMessageView(state)
        }
    }
}

@Composable
private fun FormatSelector(state: DownloadItemState) {
    val metadata = state.videoMetadata
    val format = state.downloadItemOptions.format

    val formats =
        metadata?.formats?.asReversed()
            ?: return Text("No formats available, most likely because this is a playlist")

    val selectedVideoOption by format.video.collectAsState()
    val selectedAudioOption by format.audio.collectAsState()

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
            selectionChanged = { format.selectVideoFormat(it) },
            modifier = Modifier.weight(1f),
            optionFormatter = { it?.videoDescription ?: "(No Video)" },
            label = "Video Format",
        )
        DropdownMenu(
            audioFormats,
            selectedOption = selectedAudioOption,
            selectionChanged = { format.selectAudioFormat(it) },
            modifier = Modifier.weight(1f),
            optionFormatter = { it?.audioDescription ?: "(No Audio)" },
            label = "Audio Format",
        )
    }
}

@Composable
private fun ErrorMessageView(state: DownloadItemState) {
    Text(
        state.errorMessage ?: "Unknown error",
        color = MaterialTheme.colors.error,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
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
