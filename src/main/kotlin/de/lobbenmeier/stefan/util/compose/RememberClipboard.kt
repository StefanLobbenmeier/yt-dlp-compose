package de.lobbenmeier.stefan.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.FlavorListener

val systemClipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard

private val Clipboard.text: String
    get() {
        return getData(DataFlavor.stringFlavor) as String
    }

// adapted from https://stackoverflow.com/a/73709923
@Composable
fun rememberClipboardText(): State<String?> {
    val text = remember { mutableStateOf(systemClipboard.text) }
    onClipDataChanged { text.value = it }
    return text
}

@Suppress("ComposableNaming")
@Composable
fun onClipDataChanged(onPrimaryClipChanged: (String) -> Unit) {
    val clipboardListener = remember {
        FlavorListener {
            val clipboardText = systemClipboard.text
            onPrimaryClipChanged(clipboardText)
        }
    }
    DisposableEffect(systemClipboard) {
        systemClipboard.addFlavorListener(clipboardListener)
        onDispose { systemClipboard.removeFlavorListener(clipboardListener) }
    }
}
