package de.lobbenmeier.stefan.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import de.lobbenmeier.stefan.common.ui.isSystemInDarkTheme
import de.lobbenmeier.stefan.settings.business.Appearance

private val LightColorPalette = lightColors()

private val DarkColorPalette = darkColors()

@Composable
fun AppTheme(appearance: Appearance, content: @Composable () -> Unit) {
    val darkTheme =
        appearance == Appearance.DARK || (appearance == Appearance.SYSTEM && isSystemInDarkTheme())
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(colors = colors, content = content)
}
