package de.lobbenmeier.stefan

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

val GithubJson = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalSerializationApi::class)
val YtDlpJson = Json {
    ignoreUnknownKeys = true
    namingStrategy = JsonNamingStrategy.SnakeCase
}