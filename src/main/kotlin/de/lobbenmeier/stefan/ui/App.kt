package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.lobbenmeier.stefan.model.BinariesUpdater
import de.lobbenmeier.stefan.model.DownloadQueue
import de.lobbenmeier.stefan.ytdlp.YtDlp

@Composable
fun App() {
    val binariesUpdater = remember { BinariesUpdater() }
    val ytDlp = remember { YtDlp() }
    val downloadQueue = remember { DownloadQueue() }

    Column {
        Updater(binariesUpdater.downloads)

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
