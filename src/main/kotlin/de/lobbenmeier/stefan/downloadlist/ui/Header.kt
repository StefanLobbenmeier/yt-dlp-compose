package de.lobbenmeier.stefan.downloadlist.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Plus
import compose.icons.feathericons.Settings
import de.lobbenmeier.stefan.common.ui.rememberClipboardText
import de.lobbenmeier.stefan.downloadlist.business.DownloadItem
import de.lobbenmeier.stefan.downloadlist.business.YtDlp
import de.lobbenmeier.stefan.downloadlist.business.setYtDlp
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.business.binariesSettings
import de.lobbenmeier.stefan.updater.business.BinariesUpdater
import de.lobbenmeier.stefan.updater.ui.Updater

@Composable
@Preview
fun Header(
    settings: Settings,
    onDownload: (downloadItem: DownloadItem) -> Unit,
    onSettingsButtonClicked: () -> Unit,
) {
    val binariesUpdater =
        remember(settings.binariesSettings) { BinariesUpdater(settings.binariesSettings) }
    val binaries = binariesUpdater.binaries.collectAsState().value
    val ytDlp =
        if (binaries != null) {
            YtDlp(binaries, settings).also(::setYtDlp)
        } else {
            null
        }

    val submitDownload: (String) -> Unit = { downloadUrl: String ->
        ytDlp?.createDownloadItem(downloadUrl)?.let(onDownload)
    }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
    ) {
        Row(
            modifier =
                Modifier.border(1.dp, MaterialTheme.colors.onSurface, RoundedCornerShape(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ytDlp == null) {
                Updater(Modifier.weight(1f), binariesUpdater)
            } else {
                DownloadUrlInput(Modifier.weight(1f), submitDownload)
            }

            Box(
                modifier =
                    Modifier.fillMaxHeight()
                        .aspectRatio(1f)
                        .minimumInteractiveComponentSize()
                        .clickable(onClick = onSettingsButtonClicked)
                        .semantics { role = Role.Button },
                contentAlignment = Alignment.Center,
            ) {
                Icon(FeatherIcons.Settings, "Settings")
            }
        }
    }
}

@Composable
private fun DownloadUrlInput(modifier: Modifier, submitDownload: (String) -> Unit) {
    var enteredDownloadUrl by remember { mutableStateOf("") }
    val clipboardText = rememberClipboardText().value
    val downloadUrl = getDownloadUrl(enteredDownloadUrl, clipboardText)
    val downloadButtonEnabled = downloadUrl != null

    val onSubmitDownload = {
        if (downloadUrl != null) {
            submitDownload(downloadUrl)
            enteredDownloadUrl = ""
        }
    }

    OutlinedTextField(
        enteredDownloadUrl,
        singleLine = true,
        onValueChange = { enteredDownloadUrl = it },
        placeholder = {
            Text(
                clipboardText ?: "Enter a video URL",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                softWrap = false,
            )
        },
        modifier = modifier.semantics { contentDescription = "Download URL" },
        trailingIcon = {
            IconButton(onClick = onSubmitDownload, enabled = downloadButtonEnabled) {
                Icon(FeatherIcons.Plus, "Download")
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSubmitDownload() }),
        shape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp),
    )
}

fun getDownloadUrl(enteredDownloadUrl: String, clipboardText: String?): String? {
    return enteredDownloadUrl.ifBlank { clipboardText }
}
