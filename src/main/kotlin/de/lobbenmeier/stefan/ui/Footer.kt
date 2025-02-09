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
import de.lobbenmeier.stefan.settings.ui.formatSettings
import de.lobbenmeier.stefan.settings.ui.textFieldWidth

@Composable
fun Footer(
    settings: Settings,
    updateSettings: (Settings) -> Unit,
    clearDownloads: () -> Unit,
    downloadAll: () -> Unit,
) {
    BottomAppBar(backgroundColor = MaterialTheme.colors.surface) {
        Row {
            Spacer(Modifier.weight(1f, true))
            DownloadFolderSetting(settings, updateSettings)
            AuthenticationSetting(settings, updateSettings)
            SubtitlesSetting(settings, updateSettings)
            Spacer(Modifier.weight(0.5f, true))
            FormatSelectionSetting(settings, updateSettings)
            Spacer(Modifier.weight(0.5f, true))
            // TODO Text("When done")
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
        onValueChange = { updateSettings(settings.copy(downloadFolder = it)) },
    )
}

@Composable
fun AuthenticationSetting(settings: Settings, updateSettings: (Settings) -> Unit) {
    return QuickSettingIconButton(icon = FeatherIcons.Key, contentDescription = "Authentication") {
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
            contentDescription = "Embed Subtitles ($embedSubtitlesEnabledString)",
        )
    }
}

@Composable
fun FormatSelectionSetting(settings: Settings, updateSettings: (Settings) -> Unit) {
    return QuickSettingButton("Formats", "Formats") { formatSettings(settings, updateSettings) }
}

@Composable
fun ClearDownloadQueueButton(clearDownloads: () -> Unit) {
    return IconButton(onClick = { clearDownloads() }) {
        Icon(FeatherIcons.Trash, contentDescription = "Clear download list")
    }
}

@Composable
fun DownloadAllButton(downloadAll: () -> Unit) {
    return Button(onClick = downloadAll) { Text(text = "Download") }
}

@Composable
fun QuickSettingIconButton(
    icon: ImageVector,
    contentDescription: String,
    dialogContent: @Composable () -> Unit,
) {
    var quickSettingsOpen by remember { mutableStateOf(false) }

    if (quickSettingsOpen) {
        QuickSettingsDialog(dialogContent, contentDescription, { quickSettingsOpen = false })
    }

    return IconButton(onClick = { quickSettingsOpen = !quickSettingsOpen }) {
        Icon(icon, contentDescription)
    }
}

@Composable
fun QuickSettingButton(
    buttonText: String,
    contentDescription: String,
    dialogContent: @Composable () -> Unit,
) {
    var quickSettingsOpen by remember { mutableStateOf(false) }

    if (quickSettingsOpen) {
        QuickSettingsDialog(dialogContent, contentDescription, { quickSettingsOpen = false })
    }

    return Button(onClick = { quickSettingsOpen = !quickSettingsOpen }) { Text(buttonText) }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QuickSettingsDialog(
    dialogContent: @Composable () -> Unit,
    contentDescription: String,
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformInsets = false, usePlatformDefaultWidth = false),
    ) {
        Box(
            Modifier.padding(vertical = 32.dp)
                .background(MaterialTheme.colors.surface)
                .padding(24.dp)
                .width(textFieldWidth)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(contentDescription, style = MaterialTheme.typography.h5)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription) }
                }
                Column(Modifier.padding(vertical = 8.dp), content = { dialogContent() })
            }
        }
    }
}
