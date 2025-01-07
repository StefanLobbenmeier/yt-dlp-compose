package de.lobbenmeier.stefan.updater.business.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Asset(val name: String, @SerialName("browser_download_url") val downloadUrl: String)
