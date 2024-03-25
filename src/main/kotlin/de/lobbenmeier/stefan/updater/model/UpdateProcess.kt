package de.lobbenmeier.stefan.updater.model

import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import kotlinx.coroutines.flow.MutableStateFlow

class UpdateProcess(
    val name: String,
    val progress: MutableStateFlow<UpdateDownloadProgress>,
)
