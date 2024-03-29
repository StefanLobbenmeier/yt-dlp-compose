package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.lobbenmeier.stefan.downloadlist.business.YtDlp
import de.lobbenmeier.stefan.downloadlist.model.DownloadQueue
import de.lobbenmeier.stefan.downloadlist.ui.DownloadList
import de.lobbenmeier.stefan.downloadlist.ui.Header
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.business.SettingsViewModel
import de.lobbenmeier.stefan.settings.ui.SettingsUI
import de.lobbenmeier.stefan.updater.business.BinariesUpdater
import de.lobbenmeier.stefan.updater.model.Binaries
import de.lobbenmeier.stefan.updater.ui.Updater

@Composable
fun App() {
    val settingsViewModel = remember { SettingsViewModel() }
    val settings by settingsViewModel.settings.collectAsState()
    val binariesUpdater = remember { BinariesUpdater() }
    val binaries = binariesUpdater.binaries.collectAsState().value

    if (binaries == null) {
        Updater(binariesUpdater.downloads)
    } else {
        MainView(settings, settingsViewModel::saveSettings, binaries)
    }
}

@Composable
private fun MainView(settings: Settings, updateSettings: (Settings) -> Unit, binaries: Binaries) {
    val ytDlp = remember(settings) { YtDlp(binaries, settings) }
    val downloadQueue = remember(ytDlp) { DownloadQueue(ytDlp) }

    var settingsOpen by remember { mutableStateOf(false) }

    if (settingsOpen) {
        SettingsUI(
            settings,
            {
                settingsOpen = false
                updateSettings(it)
            },
            cancel = { settingsOpen = false }
        )
    } else {
        Column {
            Scaffold(
                topBar = {
                    Header(
                        onDownload = { downloadQueue.add(it) },
                        onSettingsButtonClicked = { settingsOpen = true }
                    )
                },
                bottomBar = { Footer() }
            ) {
                DownloadList(downloadQueue)
            }
        }
    }
}

@Composable
fun Footer() {
    Column {
        Text("Ready to download")
        Row {
            Text("Folder")
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
