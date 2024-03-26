package de.lobbenmeier.stefan.settings.business

import de.lobbenmeier.stefan.common.business.SettingsJson
import de.lobbenmeier.stefan.updater.business.getPlatform
import io.github.oshai.kotlinlogging.KotlinLogging

val logger = KotlinLogging.logger {}

fun loadSettings(): Settings {
    val platform = getPlatform()
    val settingsFile = platform.settingsFile

    logger.info { """Loading settings "$settingsFile" from $platform""" }

    return createEmptySettings()
}

private fun createEmptySettings(): Settings =
    SettingsJson.decodeFromString("""{"app":  {}, "ytDlp":  {}}""")
