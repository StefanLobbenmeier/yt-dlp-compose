package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.common.ui.rememberDesktopDialogLauncher
import de.lobbenmeier.stefan.downloadlist.model.DownloadQueue
import de.lobbenmeier.stefan.downloadlist.ui.DownloadList
import de.lobbenmeier.stefan.downloadlist.ui.Header
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.business.SettingsViewModel
import de.lobbenmeier.stefan.settings.ui.SettingsUI
import de.lobbenmeier.stefan.version.CheckForAppUpdate

@Composable
fun App(settingsViewModel: SettingsViewModel) {
    val settings by settingsViewModel.settings.collectAsState()

    val downloadQueue = remember { DownloadQueue() }

    MainView(settings, settingsViewModel::saveSettings, downloadQueue)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MainView(
    settings: Settings,
    updateSettings: (Settings) -> Unit,
    downloadQueue: DownloadQueue,
) {
    val openSettings =
        rememberDesktopDialogLauncher("Settings", 480.dp, 720.dp) { close ->
            SettingsUI(
                settings,
                {
                    close()
                    updateSettings(it)
                },
                cancel = close,
            )
        }

    Scaffold(
        topBar = {
            Header(
                settings,
                onDownload = downloadQueue::add,
                onSettingsButtonClicked = openSettings,
            )
        },
        bottomBar = {
            Footer(
                settings,
                updateSettings,
                clearDownloads = downloadQueue::clear,
                downloadAll = downloadQueue::downloadAll,
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues), contentAlignment = Alignment.BottomStart) {
            DownloadList(downloadQueue)
            Box(modifier = Modifier.padding(16.dp)) { CheckForAppUpdate() }
        }
    }
}
