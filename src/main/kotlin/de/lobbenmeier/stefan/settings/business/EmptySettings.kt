package de.lobbenmeier.stefan.settings.business

import de.lobbenmeier.stefan.common.business.SettingsJson

fun createEmptySettings(): Settings = SettingsJson.decodeFromString("""{}""")
