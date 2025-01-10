package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.common.ui.rememberClipboardText

@Composable
@Preview
fun Header(onDownload: (url: String) -> Unit, onSettingsButtonClicked: () -> Unit) {
    var enteredDownloadUrl by remember { mutableStateOf("") }
    val clipboardText = rememberClipboardText().value
    val downloadUrl = getDownloadUrl(enteredDownloadUrl, clipboardText)
    val downloadButtonEnabled = downloadUrl != null

    val submitDownload = {
        if (downloadUrl != null) {
            onDownload(downloadUrl)
            enteredDownloadUrl = ""
        }
    }

    TopAppBar(backgroundColor = MaterialTheme.colors.surface) {
        Row(
            modifier =
                Modifier.border(1.dp, MaterialTheme.colors.onSurface, RoundedCornerShape(4.dp))
        ) {
            OutlinedTextField(
                enteredDownloadUrl,
                singleLine = true,
                onValueChange = { enteredDownloadUrl = it },
                placeholder = { Text(clipboardText ?: "Enter a video URL") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = submitDownload, enabled = downloadButtonEnabled) {
                        Icon(Icons.Default.Add, "Download")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { submitDownload() }),
                shape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp),
            )

            Box(
                modifier =
                    Modifier.fillMaxHeight()
                        .aspectRatio(1f)
                        .minimumInteractiveComponentSize()
                        .clickable(onClick = onSettingsButtonClicked),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Settings, "Settings")
            }
        }
    }
}

fun getDownloadUrl(enteredDownloadUrl: String, clipboardText: String?): String? {
    return enteredDownloadUrl.ifBlank { clipboardText }
}
