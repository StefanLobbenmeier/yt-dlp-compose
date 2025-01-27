package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import compose.icons.FeatherIcons
import compose.icons.feathericons.Folder
import compose.icons.feathericons.Play
import de.lobbenmeier.stefan.common.ui.SmallIconButton
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
                    Desktop.getDesktop().open(file)
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
                    Desktop.getDesktop().browseFileDirectory(file.absoluteFile)
                } else {
                    logger.error { "Could not find file $file" }
                }
            }
        },
    ) {
        Icon(FeatherIcons.Folder, "Show file in folder")
    }
}
