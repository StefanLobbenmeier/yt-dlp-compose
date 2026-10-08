package de.lobbenmeier.stefan.common.ui

import androidx.compose.runtime.Composable
import dev.nucleusframework.darkmodedetector.isSystemInDarkMode

@Composable fun isSystemInDarkTheme(): Boolean = isSystemInDarkMode()
