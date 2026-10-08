package de.lobbenmeier.stefan.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalClipboardManager
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

private val logger = KotlinLogging.logger {}

@Composable
fun rememberClipboardText(): State<String?> {
    val text = remember { mutableStateOf<String?>(null) }
    listenToClipboard { text.value = it }
    return text
}

@Composable
fun listenToClipboard(onClipboardChanged: (String?) -> Unit) {
    val clipboard = LocalClipboardManager.current
    val onChanged = rememberUpdatedState(onClipboardChanged)
    LaunchedEffect(clipboard) {
        while (true) {
            val text =
                try {
                    clipboard.getText()?.text
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    logger.warn(e) { "Failed to get clipboard" }
                    null
                }
            onChanged.value(text)
            delay(1000)
        }
    }
}
