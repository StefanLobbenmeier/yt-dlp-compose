package de.lobbenmeier.stefan.model

import androidx.compose.runtime.mutableStateListOf
import de.lobbenmeier.stefan.ytdlp.DownloadItem
import de.lobbenmeier.stefan.ytdlp.YtDlp
import de.lobbenmeier.stefan.ytdlp.YtDlpConfiguration
import de.lobbenmeier.stefan.ytdlp.YtDlpVersion

class DownloadQueue {


    val items = mutableStateListOf<DownloadItem>()
    val ytDlp = YtDlp(YtDlpConfiguration(), YtDlpVersion())

    init {
        add("https://www.youtube.com/watch?v=JKjN5mmnSX0")
    }


    fun add(url: String) {
        val downloadItem = ytDlp.createDownloadItem(url)
        items.add(downloadItem)
    }
}