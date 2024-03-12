package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import de.lobbenmeier.stefan.model.Binaries
import de.lobbenmeier.stefan.model.BinariesUpdater
import de.lobbenmeier.stefan.model.DownloadQueue
import de.lobbenmeier.stefan.ytdlp.YtDlp

@Composable
fun App() {
    val binariesUpdater = remember { BinariesUpdater() }
    val binaries = binariesUpdater.binaries.collectAsState().value

    if (binaries == null) {
        Updater(binariesUpdater.downloads)
    } else {
        MainView(binaries)
    }
}

@Composable
private fun MainView(binaries: Binaries) {
    val ytDlp = remember { YtDlp(binaries) }
    val downloadQueue = remember { DownloadQueue(ytDlp) }

    Column {
        Scaffold(
            topBar = { Header(onDownload = { downloadQueue.add(it) }) }, bottomBar = { Footer() }) {
                DownloadList(downloadQueue)
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
