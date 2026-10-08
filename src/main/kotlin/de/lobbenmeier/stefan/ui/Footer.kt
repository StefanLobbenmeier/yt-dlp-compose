package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Key
import compose.icons.feathericons.Trash
import compose.icons.feathericons.X
import de.lobbenmeier.stefan.common.ui.DesktopScrollableColumn
import de.lobbenmeier.stefan.common.ui.icons.Subtitles
import de.lobbenmeier.stefan.common.ui.icons.SubtitlesOff
import de.lobbenmeier.stefan.common.ui.rememberDesktopDialogLauncher
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.ui.DirectoryPickerButton
import de.lobbenmeier.stefan.settings.ui.authenticationSettings
import de.lobbenmeier.stefan.settings.ui.formatSettings

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
    val openDialog =
        rememberDesktopDialogLauncher(contentDescription, 480.dp, 480.dp) { close ->
            QuickSettingsDialog(dialogContent, contentDescription, close)
        }

    return IconButton(onClick = openDialog) { Icon(icon, contentDescription) }
}

@Composable
fun QuickSettingButton(
    buttonText: String,
    contentDescription: String,
    dialogContent: @Composable () -> Unit,
) {
    val openDialog =
        rememberDesktopDialogLauncher(contentDescription, 480.dp, 480.dp) { close ->
            QuickSettingsDialog(dialogContent, contentDescription, close)
        }

    return Button(onClick = openDialog) { Text(buttonText) }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QuickSettingsDialog(
    dialogContent: @Composable () -> Unit,
    contentDescription: String,
    onClose: () -> Unit,
) {
    DesktopScrollableColumn(Modifier.fillMaxSize().padding(24.dp)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(contentDescription, style = MaterialTheme.typography.h5)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onClose) { Icon(FeatherIcons.X, "Close Dialog") }
            }
            Column(Modifier.padding(vertical = 8.dp), content = { dialogContent() })
        }
    }
}
