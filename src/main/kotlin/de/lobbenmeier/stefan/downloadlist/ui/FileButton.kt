package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
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

    when (platformType) {
        PlatformType.WINDOWS -> TODO()
        PlatformType.MAC_OS -> TODO()
        PlatformType.LINUX -> TODO()
    }
}

private fun Platform.browseDirectory(file: File) {
    if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
        return Desktop.getDesktop().browseFileDirectory(file.absoluteFile)
    }

    when (platformType) {
        PlatformType.WINDOWS -> TODO()
        PlatformType.MAC_OS -> TODO()
        PlatformType.LINUX -> TODO()
    }
}
