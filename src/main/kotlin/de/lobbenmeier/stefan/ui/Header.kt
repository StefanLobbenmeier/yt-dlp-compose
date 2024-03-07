package de.lobbenmeier.stefan.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun Header(onDownload: (url: String) -> Unit) {
    var downloadUrl by remember { mutableStateOf("") }

    TopAppBar(
        title = {
            Row {
                OutlinedTextField(
                    downloadUrl,
                    singleLine = true,
                    onValueChange = { downloadUrl = it },
                    placeholder = { Text("Enter a video URL") },
                    modifier = Modifier.weight(1f, true),
                    trailingIcon = {
                        IconButton(onClick = { onDownload(downloadUrl) }) {
                            Icon(Icons.Default.Add, "Download")
                        }
                    })

                Spacer(Modifier.weight(0.05f, true))

                IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Settings") }
            }
        })
}
