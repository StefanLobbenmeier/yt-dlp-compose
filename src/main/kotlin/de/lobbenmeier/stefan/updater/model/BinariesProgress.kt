package de.lobbenmeier.stefan.updater.model

import de.lobbenmeier.stefan.downloadlist.business.UpdateDownloadProgress
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface BinariesProgress

data class LocalBinaryProgress(val name: String, val path: File) : BinariesProgress

data class RemoteBinaryProgress(
    val name: String,
    val progress: MutableStateFlow<UpdateDownloadProgress>,
) : BinariesProgress
