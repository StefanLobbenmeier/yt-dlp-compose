package de.lobbenmeier.stefan.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.settings.business.Settings

@Composable
fun SettingsUI(settings: Settings, save: (Settings) -> Unit, cancel: () -> Unit) {
    var mutableSettings by remember { mutableStateOf(settings.copy()) }

    Column(modifier = Modifier.padding(32.dp)) {
        NumberSetting("Max Concurrent Jobs", mutableSettings.maxConcurrentJobs) {
            mutableSettings = mutableSettings.copy(maxConcurrentJobs = it)
        }

        Text("World")

        Row {
            Spacer(Modifier.weight(1f, true))

            Button(onClick = cancel) { Text("Cancel") }

            Spacer(Modifier.width(16.dp))

            Button(onClick = { save(mutableSettings) }) { Text("Apply") }
        }
    }
}

@Composable
private fun NumberSetting(description: String, value: Int, setValue: (Int) -> Unit) {

    Row {
        Text(description)

        Spacer(Modifier.width(8.dp))

        NumberInput(value, onValueChange = setValue)
    }
}

@Composable
private fun NumberInput(value: Int, onValueChange: (Int) -> Unit) {
    OutlinedTextField("$value", onValueChange = { it.toIntOrNull()?.let(onValueChange) })
}
