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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.File
import compose.icons.feathericons.Folder
import de.lobbenmeier.stefan.downloadlist.ui.DropdownMenu
import de.lobbenmeier.stefan.settings.business.Appearance
import de.lobbenmeier.stefan.settings.business.DenoLocation
import de.lobbenmeier.stefan.settings.business.FfmpegLocation
import de.lobbenmeier.stefan.settings.business.Settings
import de.lobbenmeier.stefan.settings.business.YtDlpLocation
import de.lobbenmeier.stefan.updater.business.platform
import de.lobbenmeier.stefan.version.CurrentVersionUI
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.FileKitMacOSSettings
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import java.io.File
import kotlin.io.path.absolutePathString

val textFieldWidth = 350.dp

@Composable
fun SettingsUI(settings: Settings, save: (Settings) -> Unit, cancel: () -> Unit) {
    var mutableSettings by remember { mutableStateOf(settings) }

    Column(
        Modifier.padding(vertical = 32.dp)
            .background(MaterialTheme.colors.background)
            .padding(24.dp)
            .width(textFieldWidth),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Section("Application") {
                FixedChoiceInput(
                    "Appearance",
                    mutableSettings.appearance,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(appearance = it ?: Appearance.SYSTEM)
                    },
                    options = Appearance.entries,
                )
            }

            Section("Binaries") {
                FixedChoiceInput(
                    "Yt-Dlp Source",
                    mutableSettings.ytDlpSource,
                    onValueChange = { mutableSettings = mutableSettings.copy(ytDlpSource = it) },
                    options = YtDlpLocation.entries,
                )
                if (mutableSettings.ytDlpSource == YtDlpLocation.DISK) {
                    FileInput(
                        "Yt-Dlp Path",
                        mutableSettings.ytDlpPath,
                        onValueChange = { mutableSettings = mutableSettings.copy(ytDlpPath = it) },
                    )
                }

                FixedChoiceInput(
                    "Ffmpeg Source",
                    mutableSettings.ffmpegSource,
                    onValueChange = { mutableSettings = mutableSettings.copy(ffmpegSource = it) },
                    options = FfmpegLocation.entries,
                )
                if (mutableSettings.ffmpegSource == FfmpegLocation.DISK) {
                    FileInput(
                        "Ffmpeg Path",
                        mutableSettings.ffmpegPath,
                        onValueChange = { mutableSettings = mutableSettings.copy(ffmpegPath = it) },
                    )
                }

                FixedChoiceInput(
                    "Deno Source",
                    mutableSettings.denoSource,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(denoSource = it ?: DenoLocation.NONE)
                    },
                    options = DenoLocation.entries,
                )
                if (mutableSettings.denoSource == DenoLocation.DISK) {
                    FileInput(
                        "Deno Path",
                        mutableSettings.denoPath,
                        onValueChange = { mutableSettings = mutableSettings.copy(denoPath = it) },
                    )
                }
            }

            Section("Performance") {
                NumberInput(
                    "Max Concurrent Jobs",
                    mutableSettings.maxConcurrentJobs,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(maxConcurrentJobs = it)
                    },
                )
            }

            Section("Network") {
                TextInput(
                    "Proxy",
                    mutableSettings.proxy,
                    onValueChange = { mutableSettings = mutableSettings.copy(proxy = it) },
                )
                NumberInput(
                    "Concurrent fragments",
                    mutableSettings.concurrentFragments,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(concurrentFragments = it)
                    },
                )
                NumberInput(
                    "Rate limit per video in KB/s",
                    mutableSettings.rateLimit,
                    onValueChange = { mutableSettings = mutableSettings.copy(rateLimit = it) },
                )
            }
            Section("Authentication") {
                authenticationSettings(
                    settings = mutableSettings,
                    updateSettings = { mutableSettings = it },
                )
            }

            Section("Formats") {
                formatSettings(
                    settings = mutableSettings,
                    updateSettings = { mutableSettings = it },
                )
            }

            Section("Subtitles") {
                subtitleSettings(
                    settings = mutableSettings,
                    updateSettings = { mutableSettings = it },
                )
            }

            Section("Output") {
                TextInput(
                    "Merge Output Format (Fast)",
                    mutableSettings.mergeOutputFormat,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(mergeOutputFormat = it)
                    },
                )
                TextInput(
                    "Remux Output Format (Slower)",
                    mutableSettings.remuxFormat,
                    onValueChange = { mutableSettings = mutableSettings.copy(remuxFormat = it) },
                )
                TextInput(
                    "Recode Output Format (Slowest)",
                    mutableSettings.recodeFormat,
                    onValueChange = { mutableSettings = mutableSettings.copy(recodeFormat = it) },
                )
                TextInput(
                    "Audio Only Downloads Format",
                    mutableSettings.audioFormat,
                    onValueChange = { mutableSettings = mutableSettings.copy(audioFormat = it) },
                )
            }

            Section("Embeddings") {
                BooleanInput(
                    "Embed Chapters",
                    mutableSettings.embedChapters,
                    onValueChange = { mutableSettings = mutableSettings.copy(embedChapters = it) },
                )
                BooleanInput(
                    "Embed metadata",
                    mutableSettings.embedMetadata,
                    onValueChange = { mutableSettings = mutableSettings.copy(embedMetadata = it) },
                )
                BooleanInput(
                    "Embed subtitles",
                    mutableSettings.embedSubtitles,
                    onValueChange = { mutableSettings = mutableSettings.copy(embedSubtitles = it) },
                )
                BooleanInput(
                    "Embed thumbnail",
                    mutableSettings.embedThumbnail,
                    onValueChange = { mutableSettings = mutableSettings.copy(embedThumbnail = it) },
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
                    },
                )
                TextInput(
                    "Filename template",
                    mutableSettings.filenameTemplate,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(filenameTemplate = it)
                    },
                )
                Spacer(Modifier.height(12.dp))
                BooleanInput(
                    "Save thumbnail to separate image file",
                    mutableSettings.saveThumbnailToFile,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(saveThumbnailToFile = it)
                    },
                )
                BooleanInput(
                    "Save metadata to separate JSON file",
                    mutableSettings.saveMetadataToJsonFile,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(saveMetadataToJsonFile = it)
                    },
                )
                BooleanInput(
                    "Keep unmerged files",
                    mutableSettings.keepUnmergedFiles,
                    onValueChange = {
                        mutableSettings = mutableSettings.copy(keepUnmergedFiles = it)
                    },
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CurrentVersionUI()

            Spacer(Modifier.weight(1f, true))

            Button(onClick = cancel) { Text("Cancel") }
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
        placeholder = "Bearer:yourTokenHere",
    )
    ChoiceInput(
        "Cookies from browser",
        settings.cookiesFromBrowser,
        onValueChange = { updateSettings(settings.copy(cookiesFromBrowser = it)) },
        options =
            listOf("brave", "chrome", "chromium", "edge", "firefox", "opera", "safari", "vivaldi"),
    )
    FileInput(
        "Cookies from file",
        settings.cookiesFile,
        onValueChange = { updateSettings(settings.copy(cookiesFile = it)) },
    )
}

@Composable
fun formatSettings(settings: Settings, updateSettings: (Settings) -> Unit) {
    BooleanInput("Select best Video by default", settings.selectVideo) {
        updateSettings(settings.copy(selectVideo = it))
    }
    BooleanInput("Select best Audio by default", settings.selectAudio) {
        updateSettings(settings.copy(selectAudio = it))
    }
    BooleanInput("Prefer free formats (ogg, opus, webm)", settings.preferFreeFormats) {
        updateSettings(settings.copy(preferFreeFormats = it))
    }
    ChoiceInput(
        "Sort formats",
        settings.formatSort,
        onValueChange = { updateSettings(settings.copy(formatSort = it)) },
        options = listOf("res:1080", "res:720", "res:480", "res:360"),
    )
}

@Composable
fun subtitleSettings(settings: Settings, updateSettings: (Settings) -> Unit) {
    BooleanInput("Write subtitle file", settings.writeSubtitles) {
        updateSettings(settings.copy(writeSubtitles = it))
    }
    BooleanInput("Write automatically generated subtitle file", settings.writeAutomaticSubtitles) {
        updateSettings(settings.copy(writeAutomaticSubtitles = it))
    }
    ChoiceInput(
        "Subtitle format",
        settings.subtitleFormat,
        onValueChange = { updateSettings(settings.copy(subtitleFormat = it)) },
        options = listOf("best", "srt/best", "vtt/best"),
    )
    ChoiceInput(
        "Subtitle languages",
        settings.subtitleLanguages,
        onValueChange = { updateSettings(settings.copy(subtitleLanguages = it)) },
        options = listOf("all", "en", "de", "fr", "es", "it", "nl", "pt", "ru", "zh"),
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
        onValueChange = { onValueChange(it?.toUIntOrNull()) },
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
        modifier = Modifier.semantics { contentDescription = description }.width(400.dp),
        onValueChange = {
            if (it.isEmpty()) {
                onValueChange(null)
            } else {
                onValueChange(it)
            }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BooleanInput(description: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
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
            initialDirectory = getValidInitialDirectoryOrNull(value),
            platformSettings =
                FileKitPlatformSettings(macOS = FileKitMacOSSettings(resolvesAliases = false)),
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
                Icon(FeatherIcons.File, contentDescription = "Browse for file")
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
            initialDirectory = getValidInitialDirectoryOrNull(value),
            platformSettings =
                FileKitPlatformSettings(macOS = FileKitMacOSSettings(resolvesAliases = false)),
        ) { file ->
            val filePath = file?.path
            if (filePath != null) {
                onValueChange(filePath)
            }
        }

    return IconButton(onClick = { launcher.launch() }) {
        Icon(FeatherIcons.Folder, contentDescription = "Browse for directory")
    }
}

private fun getValidInitialDirectoryOrNull(value: String?): String? {
    if (value != null) {
        val file = File(value)
        if (file.exists()) {
            return file.absolutePath
        }
        // going up one level is reasonable,
        // but do not go too far recursively to not end up in nirvana
        val parentFile = file.parentFile
        if (parentFile.exists()) {
            return parentFile.absolutePath
        }
    }
    val homeFolder = platform.homeFolder.toFile()
    if (homeFolder.exists()) {
        return homeFolder.absolutePath
    }
    return null
}
