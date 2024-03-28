package de.lobbenmeier.stefan.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.settings.business.Settings

@Composable
fun SettingsUI(settings: Settings, save: (Settings) -> Unit, cancel: () -> Unit) {
    var mutableSettings by remember { mutableStateOf(settings.copy()) }

    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)) {
                Section("Performance") {
                    NumberInput(
                        "Max Concurrent Jobs",
                        mutableSettings.maxConcurrentJobs,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(maxConcurrentJobs = it)
                        })
                }

                Section("Network") {
                    TextInput(
                        "Proxy",
                        mutableSettings.proxy,
                        onValueChange = { mutableSettings = mutableSettings.copy(proxy = it) })
                    NumberInput(
                        "Rate limit per video in KB/s",
                        mutableSettings.rateLimit,
                        onValueChange = { mutableSettings = mutableSettings.copy(rateLimit = it) })
                }

                Section("Output") {
                    TextInput(
                        "Merge Output Format (Fast)",
                        mutableSettings.mergeOutputFormat,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(mergeOutputFormat = it)
                        })
                    TextInput(
                        "Remux Output Format (Slower)",
                        mutableSettings.remuxFormat,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(remuxFormat = it)
                        })
                    TextInput(
                        "Recode Output Format (Slowest)",
                        mutableSettings.recodeFormat,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(recodeFormat = it)
                        })
                    TextInput(
                        "Audio Only Downloads Format",
                        mutableSettings.audioFormat,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(audioFormat = it)
                        })
                }

                Section("Embeddings") {
                    BooleanInput(
                        "Embed Chapters",
                        mutableSettings.embedChapters,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(embedChapters = it)
                        })
                    BooleanInput(
                        "Embed metadata",
                        mutableSettings.embedMetadata,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(embedMetadata = it)
                        })
                    BooleanInput(
                        "Embed subtitles",
                        mutableSettings.embedSubtitles,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(embedSubtitles = it)
                        })
                    BooleanInput(
                        "Embed thumbnail",
                        mutableSettings.embedThumbnail,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(embedThumbnail = it)
                        })
                }

                Section("Files") {
                    TextInput(
                        "Filename template",
                        mutableSettings.filenameTemplate,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(filenameTemplate = it)
                        })
                    BooleanInput(
                        "Save thumbnail to separate image file",
                        mutableSettings.saveThumbnailToFile,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(saveThumbnailToFile = it)
                        })
                    BooleanInput(
                        "Save metadata to separate JSON file",
                        mutableSettings.saveMetadataToJsonFile,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(saveMetadataToJsonFile = it)
                        })
                    BooleanInput(
                        "Keep unmerged files",
                        mutableSettings.keepUnmergedFiles,
                        onValueChange = {
                            mutableSettings = mutableSettings.copy(keepUnmergedFiles = it)
                        })
                }

                Row(horizontalArrangement = Arrangement.End) {
                    Button(onClick = cancel) { Text("Cancel") }

                    Spacer(Modifier.width(16.dp))

                    Button(onClick = { save(mutableSettings) }) { Text("Apply") }
                }
            }
    }
}

@Composable
private fun Section(sectionTitle: String, content: @Composable (ColumnScope.() -> Unit)) {
    Column {
        Text(sectionTitle, style = MaterialTheme.typography.h5)
        Column(content = content)
    }
}

@Composable
private fun NumberInput(description: String, value: Int?, onValueChange: (Int?) -> Unit) {
    TextInput(
        description, value?.toString() ?: "", onValueChange = { onValueChange(it?.toIntOrNull()) })
}

@Composable
private fun TextInput(description: String, value: String?, onValueChange: (String?) -> Unit) {
    OutlinedTextField(
        value ?: "",
        label = { Text(description) },
        onValueChange = {
            if (it.isEmpty()) {
                onValueChange(null)
            } else onValueChange(it)
        })
}

@Composable
private fun BooleanInput(description: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(value, onValueChange)
        Box(Modifier.clickable { onValueChange(!value) }) {
            Text(description, Modifier.padding(4.dp))
        }
    }
}
