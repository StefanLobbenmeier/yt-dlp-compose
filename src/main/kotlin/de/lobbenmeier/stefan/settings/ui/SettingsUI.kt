package de.lobbenmeier.stefan.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.File
import compose.icons.feathericons.Folder
import de.lobbenmeier.stefan.downloadlist.ui.DropdownMenu
import de.lobbenmeier.stefan.settings.business.FfmpegLocation
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.business.YtDlpLocation
import de.lobbenmeier.stefan.updater.business.platform
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.FileKitMacOSSettings
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import kotlin.io.path.absolutePathString

val textFieldWidth = 350.dp

@Composable
fun SettingsUI(settings: Settings, save: (Settings) -> Unit, cancel: () -> Unit) {
    var mutableSettings by remember { mutableStateOf(settings) }

    Column(
        Modifier.padding(vertical = 32.dp)
            .background(Color.White)
            .padding(24.dp)
            .width(textFieldWidth),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Section("Binaries") {
                FixedChoiceInput(
                    "Yt-Dlp Source",
                    mutableSettings.ytDlpSource,
                    onValueChange = { mutableSettings = mutableSettings.copy(ytDlpSource = it) },
                    options = YtDlpLocation.entries
                )
                if (mutableSettings.ytDlpSource == YtDlpLocation.DISK) {
                    FileInput(
                        "Yt-Dlp Path",
                        mutableSettings.ytDlpPath,
                        onValueChange = { mutableSettings = mutableSettings.copy(ytDlpPath = it) }
                    )
                }

                FixedChoiceInput(
                    "Ffmpeg Source",
                    mutableSettings.ffmpegSource,
                    onValueChange = { mutableSettings = mutableSettings.copy(ffmpegSource = it) },
                    options = FfmpegLocation.entries
                )
                if (mutableSettings.ffmpegSource == FfmpegLocation.DISK) {
                    FileInput(
                        "Ffmpeg Path",
                        mutableSettings.ffmpegPath,
                        onValueChange = { mutableSettings = mutableSettings.copy(ffmpegPath = it) }
                    )
                }
            }

            Section("Performance") {
                NumberInput(
                    "Max Concurrent Jobs",
                    mutableSettings.maxConcurrentJobs,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(maxConcurrentJobs = it)
                    }
                )
            }

            Section("Network") {
                TextInput(
                    "Proxy",
                    mutableSettings.proxy,
                    onValueChange = { mutableSettings = mutableSettings.copy(proxy = it) }
                )
                NumberInput(
                    "Concurrent fragments",
                    mutableSettings.concurrentFragments,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(concurrentFragments = it)
                    }
                )
                NumberInput(
                    "Rate limit per video in KB/s",
                    mutableSettings.rateLimit,
                    onValueChange = { mutableSettings = mutableSettings.copy(rateLimit = it) }
                )
            }
            Section("Authentication") {
                authenticationSettings(
                    settings = mutableSettings,
                    updateSettings = { mutableSettings = it }
                )
            }

            Section("Output") {
                TextInput(
                    "Merge Output Format (Fast)",
                    mutableSettings.mergeOutputFormat,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(mergeOutputFormat = it)
                    }
                )
                TextInput(
                    "Remux Output Format (Slower)",
                    mutableSettings.remuxFormat,
                    onValueChange = { mutableSettings = mutableSettings.copy(remuxFormat = it) }
                )
                TextInput(
                    "Recode Output Format (Slowest)",
                    mutableSettings.recodeFormat,
                    onValueChange = { mutableSettings = mutableSettings.copy(recodeFormat = it) }
                )
                TextInput(
                    "Audio Only Downloads Format",
                    mutableSettings.audioFormat,
                    onValueChange = { mutableSettings = mutableSettings.copy(audioFormat = it) }
                )
            }

            Section("Embeddings") {
                BooleanInput(
                    "Embed Chapters",
                    mutableSettings.embedChapters,
                    onValueChange = { mutableSettings = mutableSettings.copy(embedChapters = it) }
                )
                BooleanInput(
                    "Embed metadata",
                    mutableSettings.embedMetadata,
                    onValueChange = { mutableSettings = mutableSettings.copy(embedMetadata = it) }
                )
                BooleanInput(
                    "Embed subtitles",
                    mutableSettings.embedSubtitles,
                    onValueChange = { mutableSettings = mutableSettings.copy(embedSubtitles = it) }
                )
                BooleanInput(
                    "Embed thumbnail",
                    mutableSettings.embedThumbnail,
                    onValueChange = { mutableSettings = mutableSettings.copy(embedThumbnail = it) }
                )
            }

            Section("Files") {
                DirectoryInput(
                    "Download Folder",
                    mutableSettings.downloadFolder,
                    onValueChange = {
                        mutableSettings =
                            mutableSettings.copy(
                                downloadFolder = it ?: platform.downloadsFolder.absolutePathString()
                            )
                    }
                )
                TextInput(
                    "Filename template",
                    mutableSettings.filenameTemplate,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(filenameTemplate = it)
                    }
                )
                Spacer(Modifier.height(12.dp))
                BooleanInput(
                    "Save thumbnail to separate image file",
                    mutableSettings.saveThumbnailToFile,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(saveThumbnailToFile = it)
                    }
                )
                BooleanInput(
                    "Save metadata to separate JSON file",
                    mutableSettings.saveMetadataToJsonFile,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(saveMetadataToJsonFile = it)
                    }
                )
                BooleanInput(
                    "Keep unmerged files",
                    mutableSettings.keepUnmergedFiles,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(keepUnmergedFiles = it)
                    }
                )
            }
        }

        Row {
            Spacer(Modifier.weight(1f, true))

            Button(onClick = cancel) { Text("Cancel") }

            Spacer(Modifier.width(16.dp))

            Button(onClick = { save(mutableSettings) }) { Text("Apply") }
        }
    }
}

@Composable
fun authenticationSettings(settings: Settings, updateSettings: (Settings) -> Unit) {
    TextInput(
        "Header",
        settings.header,
        onValueChange = { updateSettings(settings.copy(header = it)) },
        placeholder = "Bearer:yourTokenHere"
    )
    ChoiceInput(
        "Cookies from browser",
        settings.cookiesFromBrowser,
        onValueChange = { updateSettings(settings.copy(cookiesFromBrowser = it)) },
        options =
            listOf(
                "brave",
                "chrome",
                "chromium",
                "edge",
                "firefox",
                "opera",
                "safari",
                "vivaldi",
            )
    )
    FileInput(
        "Cookies from file",
        settings.cookiesFile,
        onValueChange = { updateSettings(settings.copy(cookiesFile = it)) },
    )
}

@Composable
fun Section(sectionTitle: String, content: @Composable (ColumnScope.() -> Unit)) {
    Column {
        Text(sectionTitle, style = MaterialTheme.typography.h5)
        Column(Modifier.padding(vertical = 8.dp), content = content)
    }
}

@Composable
private fun NumberInput(description: String, value: UInt?, onValueChange: (UInt?) -> Unit) {
    TextInput(
        description,
        value?.toString() ?: "",
        onValueChange = { onValueChange(it?.toUIntOrNull()) }
    )
}

@Composable
private fun <T> FixedChoiceInput(
    description: String,
    value: T?,
    onValueChange: (T?) -> Unit,
    options: List<T>,
) {
    DropdownMenu(
        options = options,
        selectedOption = value,
        selectionChanged = { onValueChange(it) },
        label = description,
        textFieldModifier = Modifier.width(textFieldWidth),
    )
}

@Composable
private fun ChoiceInput(
    description: String,
    value: String?,
    onValueChange: (String?) -> Unit,
    options: List<String>,
) {
    val nullOption = "(none)"
    DropdownMenu(
        options = listOf(nullOption) + options,
        selectedOption = value ?: nullOption,
        selectionChanged = { if (it == nullOption) onValueChange(null) else onValueChange(it) },
        onTextInput = { if (it == nullOption) onValueChange(null) else onValueChange(it) },
        label = description,
        textFieldModifier = Modifier.width(textFieldWidth),
    )
}

@Composable
private fun TextInput(
    description: String,
    value: String?,
    onValueChange: (String?) -> Unit,
    placeholder: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value ?: "",
        label = { Text(description) },
        placeholder = placeholder?.let { { Text(it) } },
        trailingIcon = trailingIcon,
        modifier = Modifier.width(400.dp),
        onValueChange = {
            if (it.isEmpty()) {
                onValueChange(null)
            } else {
                onValueChange(it)
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BooleanInput(description: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Checkbox(value, onValueChange)
        }
        Box(Modifier.clickable { onValueChange(!value) }) {
            Text(description, Modifier.padding(4.dp))
        }
    }
}

@Composable
private fun FileInput(description: String, value: String?, onValueChange: (String?) -> Unit) {
    val launcher =
        rememberFilePickerLauncher(
            title = description,
            initialDirectory = value ?: "${platform.homeFolder}/",
            platformSettings =
                FileKitPlatformSettings(
                    macOS = FileKitMacOSSettings(resolvesAliases = false),
                ),
        ) { file ->
            if (file != null) {
                onValueChange(file.path)
            }
        }

    TextInput(
        description,
        value,
        onValueChange,
        trailingIcon = {
            IconButton(onClick = { launcher.launch() }) {
                Icon(FeatherIcons.File, contentDescription = null)
            }
        },
    )
}

@Composable
private fun DirectoryInput(description: String, value: String?, onValueChange: (String?) -> Unit) {
    TextInput(
        description,
        value,
        onValueChange,
        trailingIcon = {
            DirectoryPickerButton(description, value = value, onValueChange = onValueChange)
        },
    )
}

@Composable
fun DirectoryPickerButton(description: String, value: String?, onValueChange: (String) -> Unit) {
    val launcher =
        rememberDirectoryPickerLauncher(
            title = description,
            initialDirectory = value ?: "${platform.homeFolder}/",
            platformSettings =
                FileKitPlatformSettings(
                    macOS = FileKitMacOSSettings(resolvesAliases = false),
                ),
        ) { file ->
            val filePath = file?.path
            if (filePath != null) {
                onValueChange(filePath)
            }
        }

    return IconButton(onClick = { launcher.launch() }) {
        Icon(FeatherIcons.Folder, contentDescription = description)
    }
}
