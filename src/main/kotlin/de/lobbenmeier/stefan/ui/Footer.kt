package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.icons.FeatherIcons
import compose.icons.feathericons.Key
import compose.icons.feathericons.Trash
import de.lobbenmeier.stefan.common.ui.icons.Subtitles
import de.lobbenmeier.stefan.common.ui.icons.SubtitlesOff
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.ui.DirectoryPickerButton
import de.lobbenmeier.stefan.settings.ui.authenticationSettings
import de.lobbenmeier.stefan.settings.ui.textFieldWidth

@Composable
fun Footer(
    settings: Settings,
    updateSettings: (Settings) -> Unit,
    clearDownloads: () -> Unit,
    downloadAll: () -> Unit
) {
    BottomAppBar(backgroundColor = Color.White) {
        Row {
            Spacer(Modifier.weight(1f, true))
            DownloadFolderSetting(settings, updateSettings)
            AuthenticationSetting(settings, updateSettings)
            SubtitlesSetting(settings, updateSettings)
            Spacer(Modifier.weight(0.5f, true))
            Text("Formats (Video / Audio)")
            Text("Quality / Sorting")
            Spacer(Modifier.weight(0.5f, true))
            // TODO Text("When done")
            Spacer(Modifier.weight(0.5f, true))
            ClearDownloadQueueButton(clearDownloads)
            DownloadAllButton(downloadAll)
            Spacer(Modifier.weight(1f, true))
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
    return QuickSetting(icon = FeatherIcons.Key, contentDescription = "Authentication") {
        authenticationSettings(settings, updateSettings)
    }
}

@Composable
fun SubtitlesSetting(settings: Settings, updateSettings: (Settings) -> Unit) {
    val embedSubtitlesEnabled = settings.embedSubtitles
    val embedSubtitlesEnabledString = if (embedSubtitlesEnabled) "Enabled" else "Disabled"
    val embedSubtitlesEnabledIcon = if (embedSubtitlesEnabled) Subtitles else SubtitlesOff

    return IconButton(
        onClick = { updateSettings(settings.copy(embedSubtitles = !embedSubtitlesEnabled)) }
    ) {
        Icon(
            embedSubtitlesEnabledIcon,
            contentDescription = "Embed Subtitles ($embedSubtitlesEnabledString)"
        )
    }
}

@Composable
fun ClearDownloadQueueButton(clearDownloads: () -> Unit) {
    return IconButton(onClick = { clearDownloads() }) {
        Icon(FeatherIcons.Trash, contentDescription = "Clear download list")
    }
}

@Composable
fun DownloadAllButton(downloadAll: () -> Unit) {
    return Button(onClick = { downloadAll() }, enabled = false) { Text(text = "Download") }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QuickSetting(
    icon: ImageVector,
    contentDescription: String,
    dialogContent: @Composable () -> Unit
) {
    var quickSettingsOpen by remember { mutableStateOf(false) }

    if (quickSettingsOpen) {
        Dialog(
            onDismissRequest = { quickSettingsOpen = false },
            properties =
                DialogProperties(usePlatformInsets = false, usePlatformDefaultWidth = false),
        ) {
            Box(
                Modifier.padding(vertical = 32.dp)
                    .background(Color.White)
                    .padding(24.dp)
                    .width(textFieldWidth),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(contentDescription, style = MaterialTheme.typography.h5)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { quickSettingsOpen = false }) {
                            Icon(Icons.Default.Close, contentDescription)
                        }
                    }
                    Column(Modifier.padding(vertical = 8.dp), content = { dialogContent() })
                }
            }
        }
    }

    return IconButton(
        onClick = { quickSettingsOpen = !quickSettingsOpen },
    ) {
        Icon(icon, contentDescription)
    }
}
