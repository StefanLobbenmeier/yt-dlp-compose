package de.lobbenmeier.stefan.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.lobbenmeier.stefan.settings.business.Settings

@Composable
fun SettingsUI(settings: Settings, save: (Settings) -> Unit) {
    var mutableSettings = remember(settings) { settings.copy() }

    Column {
        Text("Hello")
        Text("World")
    }
}
