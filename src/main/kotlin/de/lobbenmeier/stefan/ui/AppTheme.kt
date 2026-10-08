package de.lobbenmeier.stefan.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.lobbenmeier.stefan.common.ui.isSystemInDarkTheme
import de.lobbenmeier.stefan.settings.business.Appearance
import dev.nucleusframework.systemcolor.systemAccentColor
import dev.nucleusframework.window.NucleusDecoratedWindowTheme

val errorColor = Color(0XFFb73232) // --error-color: #b73232;

private val LightColorPalette
    get() =
        lightColors(
            primary = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
            primaryVariant = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
            secondary = Color(0xFF52a552), // --secondary-highlight-color: #52a552;
            secondaryVariant = Color(0xFF5cb85c), // --highlight-color: #5cb85c;
            background = Color(0xFFF3F3F3), // --secondary-bg-color: #eaeaea;
            surface = Color.White, // --bg-color: #ffffff
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black,
        )

private val DarkColorPalette
    get() =
        darkColors(
            background = Color(0xFF202020),
            surface = Color(0xFF202020), // Color(0xFF292929),
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
    val accent = systemAccentColor() ?: Color(0xFF3584E4)
    val onAccent = if (accent.luminance() > 0.45f) Color.Black else Color.White
    val colors =
        (if (darkTheme) DarkColorPalette else LightColorPalette).copy(
            primary = accent,
            primaryVariant = accent,
            secondary = accent,
            secondaryVariant = accent,
            onPrimary = onAccent,
            onSecondary = onAccent,
        )

    NucleusDecoratedWindowTheme(isDark = darkTheme) {
        MaterialTheme(
            colors = colors,
            typography =
                Typography(
                    defaultFontFamily = FontFamily.SansSerif,
                    body1 = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    body2 = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                    button = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                    h5 = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
                ),
            shapes =
                Shapes(
                    small = RoundedCornerShape(4.dp),
                    medium = RoundedCornerShape(6.dp),
                    large = RoundedCornerShape(8.dp),
                ),
            content = content,
        )
    }
}

@Composable
fun linkColour(): Color = if (MaterialTheme.colors.isLight) Color(0xFF1A66FF) else Color(0xFF1A66FF)
