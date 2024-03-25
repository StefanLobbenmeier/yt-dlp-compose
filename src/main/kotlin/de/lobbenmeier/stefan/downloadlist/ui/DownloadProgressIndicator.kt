package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.downloadlist.business.DownloadCompleted
import de.lobbenmeier.stefan.downloadlist.business.DownloadFailed
import de.lobbenmeier.stefan.downloadlist.business.DownloadStarted
import de.lobbenmeier.stefan.downloadlist.business.VideoDownloadProgress
import de.lobbenmeier.stefan.downloadlist.business.YtDlpDownloadProgress

@Composable
fun DownloadProgressIndicator(downloadProgress: VideoDownloadProgress) {
    if (downloadProgress is DownloadFailed) {
        return LinearProgressIndicator(
            progress = downloadProgress.progress,
            modifier = Modifier.height(20.dp).fillMaxWidth(),
            color = Color.Red)
    }
    LinearProgressIndicator(
        progress = downloadProgress.progress, modifier = Modifier.height(20.dp).fillMaxWidth())
}

@Composable
@Preview
fun DownloadProgressIndicatorStarted() {
    DownloadProgressIndicator(DownloadStarted)
}

@Composable
@Preview
fun DownloadProgressIndicatorPreview50() {
    DownloadProgressIndicator(
        YtDlpDownloadProgress(
            downloadedBytes = 100,
            totalBytes = 200,
        ))
}

@Composable
@Preview
fun DownloadProgressIndicatorCompleted() {
    DownloadProgressIndicator(DownloadCompleted)
}

@Composable
@Preview
fun DownloadProgressIndicatorFailed() {
    DownloadProgressIndicator(DownloadFailed(Exception()))
}
