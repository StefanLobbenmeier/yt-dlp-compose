package de.lobbenmeier.stefan.updater.ui

import androidx.compose.foundation.layout.Arrangement
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
import de.lobbenmeier.stefan.updater.model.BinariesProgress
import de.lobbenmeier.stefan.updater.model.LocalBinaryProgress
import de.lobbenmeier.stefan.updater.model.RemoteBinaryProgress

@Composable
fun Updater(updates: List<BinariesProgress>) {
    Row(Modifier.padding(4.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        Text("Updating binaries...")
        updates.forEach {
            when (it) {
                is RemoteBinaryProgress -> {
                    SmallDownloadProgress(it)
                }

                is LocalBinaryProgress -> {
                    Text("${it.name} uses local build at ${it.path}")
                }
            }
        }
    }
}

@Composable
private fun SmallDownloadProgress(updateProcess: RemoteBinaryProgress) {
    val progress = updateProcess.progress.collectAsState(initial = null).value
    Row {
        Text(updateProcess.name)
        Spacer(Modifier.size(10.dp))
        if (progress == null) CircularProgressIndicator(Modifier.size(20.dp))
        else
            CircularProgressIndicator(progress = progress.progress, modifier = Modifier.size(20.dp))
    }
}
