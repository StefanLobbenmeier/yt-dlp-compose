package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.ui.DirectoryPickerButton

@Composable
fun Footer(settings: Settings, updateSettings: (Settings) -> Unit) {
    BottomAppBar(backgroundColor = Color.White) {
        Column {
            Row {
                DownloadFolderSetting(settings, updateSettings)
                Text("Key")
                Text("Subtitles")
                Text("Spacer")
                Text("Media")
                Text("Quality")
                Text("Spacer")
                Text("When done")
                Text("Delete")
                Text("Download")
            }
        }
    }
}

@Composable
fun DownloadFolderSetting(settings: Settings, updateSettings: (Settings) -> Unit) {

    return DirectoryPickerButton(
        description = "Download Folder",
        value = settings.downloadFolder,
        onValueChange = { updateSettings(settings.copy(downloadFolder = it)) }
    )
}

@Composable fun QuickSettingDialog() {}
