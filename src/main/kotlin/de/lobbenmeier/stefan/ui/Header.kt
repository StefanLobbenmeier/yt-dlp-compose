package de.lobbenmeier.stefan.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun Header(onDownload: (url: String) -> Unit) {
    var downloadUrl by remember { mutableStateOf("") }

    TopAppBar(
        contentPadding = PaddingValues(20.dp),
        backgroundColor = MaterialTheme.colors.surface,

        ) {
        OutlinedTextField(
            downloadUrl,
            singleLine = true,
            onValueChange = { downloadUrl = it },
            placeholder = { Text("Enter a video URL") },
            modifier = Modifier.weight(1f),
            trailingIcon = {
                IconButton(onClick = {
                    onDownload(downloadUrl)
                }) {
                    Icon(Icons.Default.Add, "Download")
                }
            }
        )

        Spacer(Modifier.weight(0.05f))

        IconButton(onClick = {

        }) {
            Icon(Icons.Default.Settings, "Settings")
        }
    }



}