package de.lobbenmeier.stefan.ui

import androidx.compose.ui.graphics.Color
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.floats.shouldBeGreaterThanOrEqual

class AppThemeTest : AnnotationSpec() {
    @Test
    fun accentsAndButtonTextRemainReadableInBothThemes() {
        val accents =
            listOf(
                Color.Black,
                Color.White,
                Color.Red,
                Color.Green,
                Color.Blue,
                Color.Yellow,
                Color(0xFF3584E4),
                Color(0xFF808080),
            )
        for (dark in listOf(false, true)) {
            for (accent in accents) {
                val colors = desktopColors(dark, accent)
                contrastRatio(colors.primary, colors.surface) shouldBeGreaterThanOrEqual 4.5f
                contrastRatio(colors.primary, colors.background) shouldBeGreaterThanOrEqual 4.5f
                contrastRatio(colors.onPrimary, colors.primary) shouldBeGreaterThanOrEqual 4.5f
                contrastRatio(colors.onBackground, colors.background) shouldBeGreaterThanOrEqual
                    4.5f
            }
        }
    }
}
