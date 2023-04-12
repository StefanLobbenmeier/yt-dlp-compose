package de.lobbenmeier.stefan.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.ytdlp.DownloadProgress
import de.lobbenmeier.stefan.ytdlp.progress

@Composable
fun DownloadProgressIndicator(downloadProgress: DownloadProgress) {
    Box {
        LinearProgressIndicator(
            progress = downloadProgress.progress, modifier = Modifier.height(20.dp).fillMaxWidth())
    }
}

@Composable
@Preview
fun DownloadProgressIndicatorPreview() {
    DownloadProgressIndicator(
        DownloadProgress(
            downloadedBytes = 100,
            totalBytes = 200,
        ))
}
