package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import com.github.pgreze.process.process
import compose.icons.FeatherIcons
import compose.icons.feathericons.Folder
import compose.icons.feathericons.Play
import de.lobbenmeier.stefan.common.ui.SmallIconButton
import de.lobbenmeier.stefan.updater.business.Platform
import de.lobbenmeier.stefan.updater.business.PlatformType
import de.lobbenmeier.stefan.updater.business.platform
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Desktop
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val logger = KotlinLogging.logger {}

@Composable
fun OpenFileButton(file: File?) {

    SmallIconButton(
        enabled = file != null,
        onClick = {
            if (file != null) {
                if (file.exists()) {
                    platform.openFile(file)
                } else {
                    logger.error { "Could not find file $file" }
                }
            }
        },
    ) {
        Icon(FeatherIcons.Play, "Open File")
    }
}

@Composable
fun BrowseFileButton(file: File?) {

    SmallIconButton(
        enabled = file != null,
        onClick = {
            if (file != null) {
                if (file.exists()) {
                    platform.browseDirectory(file)
                } else {
                    logger.error { "Could not find file $file" }
                }
            }
        },
    ) {
        Icon(FeatherIcons.Folder, "Show file in folder")
    }
}

private fun Platform.openFile(file: File) {
    if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
        return Desktop.getDesktop().open(file)
    }

    val command =
        when (platformType) {
            PlatformType.WINDOWS -> arrayOf("explorer.exe", file.absolutePath)
            PlatformType.MAC_OS -> arrayOf("open", file.absolutePath)
            PlatformType.LINUX -> arrayOf("xdg-open", file.absolutePath)
        }

    CoroutineScope(Dispatchers.IO).launch { process(*command) }
}

private fun Platform.browseDirectory(file: File) {
    if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
        return Desktop.getDesktop().browseFileDirectory(file.absoluteFile)
    }

    val command =
        when (platformType) {
            PlatformType.WINDOWS -> arrayOf("explorer.exe", "/select,", file.absolutePath)
            PlatformType.MAC_OS -> arrayOf("open", "-r,", file.absolutePath)
            PlatformType.LINUX -> arrayOf("xdg-open", file.parentFile.absolutePath)
        }

    CoroutineScope(Dispatchers.IO).launch { process(*command) }
}
