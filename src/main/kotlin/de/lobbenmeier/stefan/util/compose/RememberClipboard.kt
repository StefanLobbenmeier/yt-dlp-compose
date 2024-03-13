package de.lobbenmeier.stefan.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import kotlinx.coroutines.delay

private val logger = KotlinLogging.logger {}
private val systemClipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard

private val Clipboard.text: String?
    get() {
        try {
            return getData(DataFlavor.stringFlavor) as String
        } catch (e: Exception) {
            logger.warn(e) { "Failed to get clipboard" }
            return null
        }
    }

@Composable
fun rememberClipboardText(): State<String?> {
    val text = remember { mutableStateOf(systemClipboard.text) }
    listenToClipboard { text.value = it }
    return text
}

@Composable
fun listenToClipboard(onClipboardChanged: (String?) -> Unit) {
    LaunchedEffect(Unit) {
        while (true) {
            val clipboardText = systemClipboard.text
            onClipboardChanged(clipboardText)
            delay(1000)
        }
    }
}
