package de.lobbenmeier.stefan.settings.business

import de.lobbenmeier.stefan.common.business.SettingsJson
import de.lobbenmeier.stefan.updater.business.getPlatform
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString

class SettingsViewModel {
    private val logger = KotlinLogging.logger {}
    private val platform = getPlatform()
    private val settingsFile = platform.settingsFile

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<Settings>
        get() = _settings

    private fun loadSettings(): Settings {
        if (settingsFile.exists()) {
            logger.info { """Loading settings "$settingsFile" from $platform""" }
            val settingsJson = settingsFile.readText()
            return SettingsJson.decodeFromString<Settings>(settingsJson)
        } else {
            logger.warn { """Settings file $settingsFile not found for $platform""" }
            return createEmptySettings()
        }
    }

    fun saveSettings(settings: Settings) {
        val settingsJson = SettingsJson.encodeToString(settings)
        settingsFile.writeText(settingsJson)

        _settings.value = settings
    }

    private fun createEmptySettings(): Settings = SettingsJson.decodeFromString("""{}""")
}
