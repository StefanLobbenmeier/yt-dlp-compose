package de.lobbenmeier.stefan.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import compose.icons.feathericons.Folder
import de.lobbenmeier.stefan.model.DownloadQueue
import de.lobbenmeier.stefan.ytdlp.*
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import java.awt.Desktop
import kotlin.time.Duration.Companion.seconds

@Composable
fun DownloadList(downloadQueue: DownloadQueue) {
    LazyColumn(modifier = Modifier.padding(8.0.dp).fillMaxSize()) {
        downloadQueue.items.asReversed().forEach { item { DownloadItemView(it) } }
    }
}

@Composable
private fun DownloadItemView(downloadItem: DownloadItem) {
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
                    if (metadata == null) {
                        Text("Downloading metadata...")
                    } else {
                        Row {
                            Row(modifier = Modifier.weight(1f)) {
                                Text("Duration: ", fontWeight = FontWeight.Bold)
                                Text(durationString(metadata?.duration))
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
                }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column {
                IconButton(
                    onClick = { downloadItem.download(selectedVideoOption, selectedAudioOption) }) {
                        Icon(FeatherIcons.Download, "Download")
                    }
                OpenFileButton(downloadItem)
            }
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
                optionBuilder = { Text("${it.height}p${it.fps} (${it.vcodec})") })
            DropdownMenu(
                audioFormats,
                selectedOption = selectedAudioOption,
                selectionChanged = { downloadItem.selectFormat(it) },
                modifier = Modifier.weight(1f),
                optionBuilder = {
                    var text = "${it.formatNote}"
                    if (it.acodec != null) {
                        text += "(${it.acodec})"
                    }
                    Text(text)
                })
        }
    } else {
        LinearProgressIndicator(Modifier.fillMaxWidth())
    }
}

private fun durationString(i: Double?): String {
    if (i == null) {
        return ""
    }
    val duration = i.seconds
    return duration.toString()
}

fun fileSizeString(bytes: Double) =
    when {
        bytes >= 1 shl 30 -> "%.1f GB".format(bytes / (1 shl 30))
        bytes >= 1 shl 20 -> "%.1f MB".format(bytes / (1 shl 20))
        bytes >= 1 shl 10 -> "%.0f kB".format(bytes / (1 shl 10))
        else -> "$bytes bytes"
    }

@Composable
private fun Thumbnail(thumbnail: String?) {
    val modifier = Modifier.aspectRatio(16f / 9f)

    if (thumbnail != null) {
        val painterResource = lazyPainterResource(data = thumbnail)
        KamelImage(
            resource = painterResource,
            contentDescription = "Profile",
            modifier = modifier,
            onLoading = { progress -> ProgressIndicator(modifier, progress) })
    } else {
        ProgressIndicator(modifier)
    }
}

@Composable
private fun ProgressIndicator(modifier: Modifier, progress: Float? = null) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
            if (progress == null || progress < 0.3f) {
                CircularProgressIndicator()
            } else {
                CircularProgressIndicator(progress)
            }
        }
}

@Composable
private fun OpenFileButton(downloadItem: DownloadItem) {
    val file by downloadItem.targetFile.collectAsState()
    val finalFile = file

    IconButton(
        enabled = finalFile != null,
        onClick = {
            if (finalFile != null) {
                if (finalFile.exists()) {
                    Desktop.getDesktop().open(finalFile)
                } else {
                    TODO(
                        "what to do when the file is not found or yt-dlp gave us the wrong name for some reason")
                }
            }
        }) {
            Icon(FeatherIcons.Folder, "Open File")
        }
}

@Preview
@Composable
private fun DownloadItemPreview() {
    val downloadItem =
        YtDlp(YtDlpConfiguration(), YtDlpVersion())
            .createDownloadItem("https://www.youtube.com/watch?v=JKjN5mmnSX0")
    DownloadItemView(downloadItem)
}
