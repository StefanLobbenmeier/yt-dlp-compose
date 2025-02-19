package de.lobbenmeier.stefan.common.ui

import com.github.tkuenneth.nativeparameterstoreaccess.Dconf
import com.github.tkuenneth.nativeparameterstoreaccess.Dconf.HAS_DCONF
import com.github.tkuenneth.nativeparameterstoreaccess.MacOSDefaults
import com.github.tkuenneth.nativeparameterstoreaccess.NativeParameterStoreAccess.IS_MACOS
import com.github.tkuenneth.nativeparameterstoreaccess.NativeParameterStoreAccess.IS_WINDOWS
import com.github.tkuenneth.nativeparameterstoreaccess.WindowsRegistry
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

fun isSystemInDarkTheme(): Boolean =
    try {
        when {
            IS_WINDOWS -> {
                val result =
                    WindowsRegistry.getWindowsRegistryEntry(
                        "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                        "AppsUseLightTheme",
                    )
                result == 0x0
            }

            IS_MACOS -> {
                val result = MacOSDefaults.getDefaultsEntry("AppleInterfaceStyle")
                result == "Dark"
            }

            HAS_DCONF -> {
                val result = Dconf.getDconfEntry("/org/gnome/desktop/interface/gtk-theme")
                result.lowercase().contains("dark")
            }

            else -> {
                logger.warn {
                    "Could not detect dark theme on operating system ${System.getProperty("os.name")}"
                }
                false
            }
        }
    } catch (e: Exception) {
        logger.error(e) { "Failed to detect if system is in dark theme" }
        false
    }
