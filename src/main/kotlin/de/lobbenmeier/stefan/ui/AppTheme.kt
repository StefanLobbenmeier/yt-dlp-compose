package de.lobbenmeier.stefan.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.lobbenmeier.stefan.common.ui.isSystemInDarkTheme
import de.lobbenmeier.stefan.settings.business.Appearance

private val LightColorPalette =
    lightColors(
        primary = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
        primaryVariant = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
        secondary = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
        secondaryVariant = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
        background = Color(0xFFEAEAEA), // --secondary-bg-color: #eaeaea;
        surface = Color.White, // --bg-color: #ffffff
        onPrimary = Color(0XFF3C3C3C), // --font-color: #3c3c3c;
        onSecondary = Color.Black,
        onBackground = Color(0XFF3C3C3C), // --font-color: #3c3c3c;
        onSurface = Color(0xFFdcdcdc), //    --tertiary-bg-color: #dcdcdc;
    )

private val DarkColorPalette =
    darkColors(
        primary = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
        primaryVariant = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
        secondary = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
        secondaryVariant = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
        background = Color(0xFF292929), // --secondary-bg-color: #292929;
        surface = Color(0xFF212121), //  --bg-color: #212121;
        onPrimary = Color.Black,
        onSecondary = Color.White,
        onBackground = Color.White, // --font-color: #fff
        onSurface = Color(0xFF191919), //    --tertiary-bg-color: #191919;
    )

@Composable
fun AppTheme(appearance: Appearance, content: @Composable () -> Unit) {
    val darkTheme =
        appearance == Appearance.DARK || (appearance == Appearance.SYSTEM && isSystemInDarkTheme())
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(colors = colors, content = content)
}
