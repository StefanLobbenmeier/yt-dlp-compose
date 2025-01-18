package de.lobbenmeier.stefan.updater.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.downloadlist.business.DownloadStarted
import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import de.lobbenmeier.stefan.updater.business.BinariesUpdater

@Composable
fun Updater(modifier: Modifier, updater: BinariesUpdater) {
    var ytDlpDownloadProgress by remember {
        mutableStateOf<UpdateDownloadProgress>(DownloadStarted)
    }
    var ffmpegDownloadProgress by remember {
        mutableStateOf<UpdateDownloadProgress>(DownloadStarted)
    }
    var ffprobeDownloadProgress by remember {
        mutableStateOf<UpdateDownloadProgress>(DownloadStarted)
    }

    LaunchedEffect(updater) {
        updater.downloadOrFindBinaries(
            ytDlpProgress = { ytDlpDownloadProgress = it },
            ffmpegProgress = { ffmpegDownloadProgress = it },
            ffprobeProgress = { ffprobeDownloadProgress = it },
        )
    }

    Row(modifier, horizontalArrangement = Arrangement.SpaceAround) {
        Text("Updating binaries...")
        DownloadProgress("yt-dlp", ytDlpDownloadProgress)
        DownloadProgress("ffmpeg", ffmpegDownloadProgress)
        DownloadProgress("ffprobe", ffprobeDownloadProgress)
    }
}

@Composable
private fun DownloadProgress(name: String, progress: UpdateDownloadProgress) {
    Row {
        Text(name)
        SmallDownloadProgress(progress)
    }
}

@Composable
private fun SmallDownloadProgress(progress: UpdateDownloadProgress) {
    if (progress == DownloadStarted) CircularProgressIndicator(Modifier.size(20.dp))
    else CircularProgressIndicator(progress = progress.progress, modifier = Modifier.size(20.dp))
}
