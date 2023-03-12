package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    Scaffold (
        topBar = { Header() },
        bottomBar = { Footer() }
    ) { DownloadList() }
}

@Composable
fun DownloadList() {
    val downloadList = List(20) {
        "Download Item $it"
    }
    LazyColumn (modifier = Modifier.padding(8.0.dp).fillMaxSize()) {
        downloadList.forEach {
            item { Text(it) }
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
