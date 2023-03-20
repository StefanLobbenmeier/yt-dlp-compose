package de.lobbenmeier.stefan.ui

import VideoMetadata
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import de.lobbenmeier.stefan.model.DownloadQueue
import de.lobbenmeier.stefan.ytdlp.DownloadItem
import de.lobbenmeier.stefan.ytdlp.YtDlp
import de.lobbenmeier.stefan.ytdlp.YtDlpConfiguration
import de.lobbenmeier.stefan.ytdlp.YtDlpVersion
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlin.time.Duration.Companion.seconds

@Composable
fun DownloadList(downloadQueue: DownloadQueue) {
    LazyColumn(modifier = Modifier.padding(8.0.dp).fillMaxSize()) {
        downloadQueue.items.forEach { item { DownloadItemView(it) } }
    }
}

@Composable
private fun DownloadItemView(downloadItem: DownloadItem) {
    val metadata by downloadItem.metadata.collectAsState()
    val thumbnail = metadata?.thumbnail

    Card {
        Row(Modifier.requiredHeight(135.dp)) {
            Thumbnail(thumbnail)
            Column(
                Modifier.weight(1f).padding(20.dp, 15.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween) {
                    Text(metadata?.title ?: downloadItem.url, fontSize = 1.1.em)
                    FormatSelector(downloadItem, metadata)
                    if (metadata == null) {
                        Text("Downloading metadata...")
                    } else {
                        Row {
                            Row(modifier = Modifier.weight(1f)) {
                                Text("Duration: ", fontWeight = FontWeight.Bold)
                                Text(durationString(metadata?.duration))
                            }
                            Row(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Size: ",
                                    fontWeight = FontWeight.Bold,
                                )
                                Text("5MB")
                            }
                        }
                    }
                }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column {
                IconButton(onClick = downloadItem::download) {
                    Icon(FeatherIcons.Download, "Download")
                }
            }
        }
    }
}

@Composable
private fun FormatSelector(downloadItem: DownloadItem, metadata: VideoMetadata?) {
    val formats = metadata?.formats?.asReversed()

    if (formats != null) {
        val videoFormats = formats.filter { it.vcodec != "none" }

        var selectedVideoOption by remember { mutableStateOf(videoFormats[0]) }
        val audioFormats = formats.filter { it.acodec != "none" }
        var selectedAudioOption by remember { mutableStateOf(audioFormats[0]) }
        Row {
            DropdownMenu(
                videoFormats,
                selectedOption = selectedVideoOption,
                selectionChanged = { selectedVideoOption = it },
                modifier = Modifier.weight(1f),
                optionBuilder = { Text("${it.height}p${it.fps} (${it.vcodec})") })
            DropdownMenu(
                audioFormats,
                selectedOption = selectedAudioOption,
                selectionChanged = { selectedAudioOption = it },
                modifier = Modifier.weight(1f),
                optionBuilder = { Text("${it.formatNote} (${it.acodec})") })
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

@Preview
@Composable
private fun DownloadItemPreview() {
    val downloadItem =
        YtDlp(YtDlpConfiguration(), YtDlpVersion())
            .createDownloadItem("https://www.youtube.com/watch?v=JKjN5mmnSX0")
    DownloadItemView(downloadItem)
}
