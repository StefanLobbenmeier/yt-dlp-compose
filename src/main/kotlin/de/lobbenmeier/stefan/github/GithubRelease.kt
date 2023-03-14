package de.lobbenmeier.stefan.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRelease(@SerialName("tag_name") val tagName: String, val assets: List<Asset>)
