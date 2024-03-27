package de.lobbenmeier.stefan.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.settings.business.Settings

@Composable
fun SettingsUI(settings: Settings, save: (Settings) -> Unit, cancel: () -> Unit) {
    var mutableSettings = remember(settings) { settings.copy() }

    Column(modifier = Modifier.padding(32.dp)) {
        Text("Hello")
        Text("World")

        Row {
            Spacer(Modifier.weight(1f, true))

            Button(onClick = cancel) { Text("Cancel") }

            Spacer(Modifier.width(16.dp))

            Button(onClick = { save(mutableSettings) }) { Text("Apply") }
        }
    }
}
