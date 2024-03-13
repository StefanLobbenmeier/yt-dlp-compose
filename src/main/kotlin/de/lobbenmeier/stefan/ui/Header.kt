package de.lobbenmeier.stefan.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
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
                IconButton(
                    onClick = {
                        onDownload(downloadUrl)
                        downloadUrl = ""
                    }) {
                        Icon(Icons.Default.Add, "Download")
                    }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions =
                KeyboardActions(
                    onSearch = {
                        onDownload(downloadUrl)
                        downloadUrl = ""
                    }),
        )

        Spacer(Modifier.weight(0.05f))

        IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Settings") }
    }
}
