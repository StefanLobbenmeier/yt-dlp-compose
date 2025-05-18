package de.lobbenmeier.stefan.version

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.X
import de.lobbenmeier.stefan.ui.linkColour
import de.lobbenmeier.stefan.updater.business.github.GithubRelease
import de.lobbenmeier.stefan.updater.business.github.GithubReleaseDownloader
import de.lobbenmeier.stefan.updater.business.platform
import io.github.oshai.kotlinlogging.KotlinLogging

val logger = KotlinLogging.logger {}

@Composable
fun CheckForAppUpdate() {
    var newerReleaseNotFinal by remember { mutableStateOf<GithubRelease?>(null) }
    val newerRelease = newerReleaseNotFinal

    LaunchedEffect(Unit) {
        logger.info { "Checking for app update" }
        val githubReleaseDownloader =
            GithubReleaseDownloader("StefanLobbenmeier", "yt-dlp-compose", platform.downloadsFolder)
        try {
            val githubReleases = githubReleaseDownloader.getGithubReleases()

            logger.info { "Found releases: $githubReleases" }

            newerReleaseNotFinal = githubReleases.firstOrNull { it.tagName > currentVersion }
        } catch (e: Exception) {
            logger.error(e) { "Failed to check for updates" }
        }
    }

    val openUri = LocalUriHandler.current::openUri
    if (newerRelease != null) {
        Card(modifier = Modifier.width(275.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    Text(text = "Update available", style = MaterialTheme.typography.h6)
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = FeatherIcons.X,
                        "Close",
                        modifier = Modifier.clickable { newerReleaseNotFinal = null },
                    )
                }

                Text("Update ${newerRelease.tagName} is out now")
                Text(
                    style =
                        TextStyle(textDecoration = TextDecoration.Underline, color = linkColour()),
                    text = "Download on GitHub",
                    modifier =
                        Modifier.clickable { openUri(newerRelease.htmlUrl) }
                            .semantics { role = Role.Button },
                )
            }
        }
    }
}
