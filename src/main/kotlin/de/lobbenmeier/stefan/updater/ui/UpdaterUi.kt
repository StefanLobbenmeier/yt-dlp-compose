package de.lobbenmeier.stefan.updater.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.updater.business.detectOnPath
import de.lobbenmeier.stefan.updater.business.getBashPath
import de.lobbenmeier.stefan.updater.model.UpdateProcess
import kotlinx.coroutines.runBlocking

@Composable
fun Updater(updates: List<UpdateProcess>) {
    Column(Modifier.padding(4.dp).fillMaxWidth(), verticalArrangement = Arrangement.SpaceAround) {
        Text("path variable is" + System.getenv("PATH"))

        val (ytDlpPath, ffmpegPath, bashPath) =
            runBlocking { listOf(detectOnPath("yt-dlp"), detectOnPath("ffmpeg"), getBashPath()) }
        Text("which yt-dlp is " + ytDlpPath)
        Text("which ffmpeg is " + ffmpegPath)
        Text("bash path is " + bashPath)
        updates.forEach { SmallDownloadProgress(it) }
    }
}

@Composable
private fun SmallDownloadProgress(updateProcess: UpdateProcess) {
    val progress = updateProcess.progress.collectAsState(initial = null).value
    Row {
        Text(updateProcess.name)
        Spacer(Modifier.size(10.dp))
        if (progress == null) CircularProgressIndicator(Modifier.size(20.dp))
        else
            CircularProgressIndicator(progress = progress.progress, modifier = Modifier.size(20.dp))
    }
}
