package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.model.Binaries
import de.lobbenmeier.stefan.platform.getPlatform
import de.lobbenmeier.stefan.ytdlp.UpdateDownloadProgress

@Composable fun FullscreenUpdater(binaries: Binaries) {}

@Composable
fun Updater(updates: List<UpdateDownloadProgress>) {
    val platform = getPlatform()

    Column {
        Text(platform.name)

        updates.forEach { SmallDownloadProgress(it) }
    }
}

@Composable
private fun SmallDownloadProgress(updateDownloadProgress: UpdateDownloadProgress) {
    Row {
        Text("ytdlp updater")
        Spacer(Modifier.weight(1f))
        CircularProgressIndicator(Modifier.size(20.dp))
    }
}
