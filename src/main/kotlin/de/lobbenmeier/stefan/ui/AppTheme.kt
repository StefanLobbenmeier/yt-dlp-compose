package de.lobbenmeier.stefan.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.lobbenmeier.stefan.common.ui.isSystemInDarkTheme
import de.lobbenmeier.stefan.settings.business.Appearance

val errorColor = Color(0XFFb73232) // --error-color: #b73232;

private val LightColorPalette
    get() =
        lightColors(
            primary = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
            primaryVariant = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
            secondary = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
            secondaryVariant = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
            background = Color(0xFFEAEAEA), // --secondary-bg-color: #eaeaea;
            surface = Color.White, // --bg-color: #ffffff
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black,
        )

private val DarkColorPalette
    get() =
        darkColors(
            background = Color(0xFF212121),
            surface = Color(0xFF212121), // Color(0xFF292929),
            primary = Color(0xFF5cb85c),
            primaryVariant = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
            secondary = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
            secondaryVariant = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
            onPrimary = Color.Black,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
        )

@Composable
fun AppTheme(appearance: Appearance, content: @Composable () -> Unit) {
    val darkTheme =
        appearance == Appearance.DARK || (appearance == Appearance.SYSTEM && isSystemInDarkTheme())
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(colors = colors, content = content)
}

@Composable
fun linkColour(): Color = if (MaterialTheme.colors.isLight) Color(0xFF1A66FF) else Color(0xFF1A66FF)
