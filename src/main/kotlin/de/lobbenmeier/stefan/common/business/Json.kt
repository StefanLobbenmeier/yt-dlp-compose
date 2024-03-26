package de.lobbenmeier.stefan.common.business

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

val GithubJson = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalSerializationApi::class)
val YtDlpJson = Json {
    explicitNulls = false
    ignoreUnknownKeys = true
    namingStrategy = JsonNamingStrategy.SnakeCase
}

@OptIn(ExperimentalSerializationApi::class)
val SettingsJson = Json {
    explicitNulls = false
    ignoreUnknownKeys = true
}
