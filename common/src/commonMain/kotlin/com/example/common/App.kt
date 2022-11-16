package com.example.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    Column(modifier = Modifier.padding(8.0.dp)) {
        Header()
        DownloadList()
        Footer()
    }
}

@Composable
fun Header() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(8.0.dp)) {
        var downloadUrl by remember { mutableStateOf("") }
        TextField(downloadUrl, maxLines = 1, onValueChange = {downloadUrl = it},
                modifier = Modifier.weight(1F).fillMaxWidth().padding(8.dp),)
    }
}

@Composable
fun DownloadList() {
    val downloadList = List(20) {
        "Download Item $it"
    }
    LazyColumn (modifier = Modifier.padding(8.0.dp)) {
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
