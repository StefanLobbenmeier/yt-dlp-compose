package de.lobbenmeier.stefan.update

import de.lobbenmeier.stefan.ytdlp.UpdateDownloadProgress
import kotlinx.coroutines.flow.MutableStateFlow

class UpdateProcess(
    val name: String,
    val progress: MutableStateFlow<UpdateDownloadProgress>,
)
