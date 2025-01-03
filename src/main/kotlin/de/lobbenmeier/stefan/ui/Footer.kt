package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.icons.FeatherIcons
import compose.icons.feathericons.Key
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.ui.DirectoryPickerButton

@Composable
fun Footer(settings: Settings, updateSettings: (Settings) -> Unit) {
    BottomAppBar(backgroundColor = Color.White) {
        Column {
            Row {
                DownloadFolderSetting(settings, updateSettings)
                AuthenticationSetting(settings, updateSettings)
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

@Composable
fun AuthenticationSetting(settings: Settings, updateSettings: (Settings) -> Unit) {
    return QuickSetting(icon = { Icon(FeatherIcons.Key, contentDescription = "Authentication") }) {}
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QuickSetting(icon: @Composable () -> Unit, dialogContent: @Composable () -> Unit) {
    var quickSettingsOpen by remember { mutableStateOf(false) }

    if (quickSettingsOpen) {
        Dialog(
            onDismissRequest = { quickSettingsOpen = false },
            properties =
                DialogProperties(usePlatformInsets = false, usePlatformDefaultWidth = false),
        ) {
            dialogContent()
        }
    }

    return IconButton(
        onClick = { quickSettingsOpen = !quickSettingsOpen },
    ) {
        icon()
    }
}
