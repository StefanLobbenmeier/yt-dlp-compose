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

/** Keep accent-colored labels and filled buttons readable for any OS accent. */
internal fun desktopColors(dark: Boolean, systemAccent: Color): androidx.compose.material.Colors {
    val base =
        if (dark) darkColors(background = Color(0xFF202020), surface = Color(0xFF202020))
        else lightColors(background = Color(0xFFF3F3F3), surface = Color.White)
    val accent =
        accessibleAccent(systemAccent.copy(alpha = 1f), base.background, base.surface, dark)
    val onAccent =
        if (contrastRatio(accent, Color.Black) >= contrastRatio(accent, Color.White)) Color.Black
        else Color.White
    return base.copy(
        primary = accent,
        primaryVariant = accent,
        secondary = accent,
        secondaryVariant = accent,
        onPrimary = onAccent,
        onSecondary = onAccent,
    )
}

internal fun contrastRatio(first: Color, second: Color): Float {
    val a = first.luminance()
    val b = second.luminance()
    return (maxOf(a, b) + 0.05f) / (minOf(a, b) + 0.05f)
}

private fun accessibleAccent(
    accent: Color,
    background: Color,
    surface: Color,
    dark: Boolean,
): Color {
    fun readable(color: Color) =
        contrastRatio(color, background) >= 4.5f && contrastRatio(color, surface) >= 4.5f
    if (readable(accent)) return accent
    val target = if (dark) Color.White else Color.Black
    var low = 0f
    var high = 1f
    repeat(20) {
        val middle = (low + high) / 2
        if (readable(androidx.compose.ui.graphics.lerp(accent, target, middle))) high = middle
        else low = middle
    }
    return androidx.compose.ui.graphics.lerp(accent, target, high)
}

@Composable
fun AppTheme(appearance: Appearance, content: @Composable () -> Unit) {
    val darkTheme =
        appearance == Appearance.DARK || (appearance == Appearance.SYSTEM && isSystemInDarkTheme())
    val colors = desktopColors(darkTheme, systemAccentColor() ?: Color(0xFF3584E4))

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

@Composable fun linkColour(): Color = MaterialTheme.colors.primary
