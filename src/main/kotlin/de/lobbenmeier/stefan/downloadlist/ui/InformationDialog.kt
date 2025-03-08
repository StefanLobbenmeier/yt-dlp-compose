package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.lobbenmeier.stefan.common.ui.LogTextField
import de.lobbenmeier.stefan.downloadlist.business.DownloadItemState
import de.lobbenmeier.stefan.downloadlist.business.videoMetadata

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InformationDialog(state: DownloadItemState, onClose: () -> Unit) {

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformInsets = false, usePlatformDefaultWidth = false),
    ) {
        Column(Modifier.background(MaterialTheme.colors.background).width(600.dp).padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(state.videoMetadata?.title ?: state.url, style = MaterialTheme.typography.h5)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, "Close dialog") }
            }

            Column {
                Text("Logs", style = MaterialTheme.typography.h5)

                Logs(state)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Logs(state: DownloadItemState) {
    val logs = remember { state.logs }.joinToString("\n")

    Column { LogTextField(value = logs) }
}
