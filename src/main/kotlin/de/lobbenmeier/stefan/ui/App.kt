package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.lobbenmeier.stefan.model.Binaries
import de.lobbenmeier.stefan.model.DownloadQueue
import de.lobbenmeier.stefan.ytdlp.DownloadStarted

@Composable
fun App() {
    val downloadQueue = remember { DownloadQueue() }
    val binaries = remember { Binaries() }

    Column {
        binaries.downloads.forEach {
            val progress by it.progress.collectAsState(DownloadStarted)

            Text("${it.name} $progress")
        }

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
