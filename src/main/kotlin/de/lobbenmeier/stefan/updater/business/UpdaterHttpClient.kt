package de.lobbenmeier.stefan.updater.business

import de.lobbenmeier.stefan.common.business.GithubJson
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.time.Duration.Companion.seconds

val updaterHttpClient = HttpClient {
    install(HttpRequestRetry) {
        retryOnException(retryOnTimeout = true, maxRetries = 100)
        exponentialDelay(maxDelayMs = 10.seconds.inWholeMilliseconds)
    }
    install(ContentNegotiation) { json(GithubJson) }
}
