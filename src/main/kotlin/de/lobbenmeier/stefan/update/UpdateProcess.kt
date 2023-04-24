package de.lobbenmeier.stefan.update

import de.lobbenmeier.stefan.ytdlp.UpdateDownloadProgress
import kotlinx.coroutines.flow.Flow

class UpdateProcess(
    val name: String,
    val progress: Flow<UpdateDownloadProgress>,
)
