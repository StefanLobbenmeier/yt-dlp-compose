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
            Column(Modifier.weight(1f).padding(20.dp)) {
                Text(metadata?.title ?: downloadItem.url)
                FormatSelector(downloadItem)
                if (metadata == null) {
                    Text("Downloading metadata...")
                } else {
                    Row {
                        Row {
                            Text("Duration: ", fontWeight = FontWeight.Bold)
                            Text(durationString(metadata?.duration))
                        }
                        Text("Size")
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
private fun FormatSelector(downloadItem: DownloadItem) {
    Row {
        DropdownMenu(
            listOf("Video + Audio", "Video only", "Audio only"), modifier = Modifier.weight(1f)) {
                //        downloadItem.setAudioVideo()
            }
        DropdownMenu(listOf("Format1", "Format2"), modifier = Modifier.weight(1f)) {
            //        downloadItem.setFormat()
        }
    }
}

private fun durationString(i: Int?): String {
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
