package de.lobbenmeier.stefan.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import compose.icons.FeatherIcons
import compose.icons.feathericons.Folder
import compose.icons.feathericons.Play
import java.awt.Desktop
import java.io.File

@Composable
fun OpenFileButton(file: File?) {

    IconButton(
        enabled = file != null,
        onClick = {
            if (file != null) {
                if (file.exists()) {
                    Desktop.getDesktop().open(file)
                } else {
                    TODO(
                        "what to do when the file is not found or yt-dlp gave us the wrong name for some reason")
                }
            }
        }) {
            Icon(FeatherIcons.Play, "Open File")
        }
}

@Composable
fun BrowseFileButton(file: File?) {

    IconButton(
        enabled = file != null,
        onClick = {
            if (file != null) {
                if (file.exists()) {
                    Desktop.getDesktop().browseFileDirectory(file.absoluteFile)
                } else {
                    TODO(
                        "what to do when the file is not found or yt-dlp gave us the wrong name for some reason")
                }
            }
        }) {
            Icon(FeatherIcons.Folder, "Show file in folder")
        }
}
