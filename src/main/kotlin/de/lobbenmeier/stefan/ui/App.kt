package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.lobbenmeier.stefan.downloadlist.business.YtDlp
import de.lobbenmeier.stefan.downloadlist.business.setYtDlp
import de.lobbenmeier.stefan.downloadlist.model.DownloadQueue
import de.lobbenmeier.stefan.downloadlist.ui.DownloadList
import de.lobbenmeier.stefan.downloadlist.ui.Header
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.business.SettingsViewModel
import de.lobbenmeier.stefan.settings.business.binariesSettings
import de.lobbenmeier.stefan.settings.ui.SettingsUI
import de.lobbenmeier.stefan.updater.business.BinariesUpdater
import de.lobbenmeier.stefan.updater.model.Binaries
import de.lobbenmeier.stefan.updater.ui.Updater
import de.lobbenmeier.stefan.version.CheckForAppUpdate

@Composable
fun App() {
    val settingsViewModel = remember { SettingsViewModel() }
    val settings by settingsViewModel.settings.collectAsState()
    val binariesUpdater =
        remember(settings.binariesSettings) { BinariesUpdater(settings.binariesSettings) }

    val binaries = binariesUpdater.binaries.collectAsState().value
    val downloadQueue = remember { DownloadQueue() }

    if (binaries == null) {
        Updater(binariesUpdater)
    } else {
        MainView(settings, settingsViewModel::saveSettings, binaries, downloadQueue)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MainView(
    settings: Settings,
    updateSettings: (Settings) -> Unit,
    binaries: Binaries,
    downloadQueue: DownloadQueue,
) {
    val ytDlp = remember(settings) { YtDlp(binaries, settings).also(::setYtDlp) }
    var settingsOpen by remember { mutableStateOf(false) }

    if (settingsOpen) {
        Dialog(
            onDismissRequest = { settingsOpen = false },
            properties =
                DialogProperties(usePlatformInsets = false, usePlatformDefaultWidth = false),
        ) {
            SettingsUI(
                settings,
                {
                    settingsOpen = false
                    updateSettings(it)
                },
                cancel = { settingsOpen = false },
            )
        }
    }

    Column {
        Scaffold(
            topBar = {
                Header(
                    onDownload = { downloadQueue.add(ytDlp.createDownloadItem(it)) },
                    onSettingsButtonClicked = { settingsOpen = true },
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
            Box(
                modifier = Modifier.padding(paddingValues),
                contentAlignment = Alignment.BottomStart,
            ) {
                DownloadList(downloadQueue)
                Box(modifier = Modifier.padding(16.dp)) { CheckForAppUpdate() }
            }
        }
    }
}
