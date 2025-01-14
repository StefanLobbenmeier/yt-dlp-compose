package de.lobbenmeier.stefan.version

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun CurrentVersionUI() {
    return Text("Version: $currentVersion")
}

val currentVersion by lazy {
    object {}.javaClass.getResourceAsStream("/version.txt")?.readBytes()?.decodeToString()
        ?: "UNKNOWN"
}
