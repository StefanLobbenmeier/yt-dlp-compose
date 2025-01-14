package de.lobbenmeier.stefan.version

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun CheckForAppUpdate() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // delay(1000)
        visible = true
    }

    val openUri = LocalUriHandler.current::openUri
    if (visible) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    Text(text = "Update available", style = MaterialTheme.typography.h6)
                    Icon(
                        imageVector = Icons.Default.Close,
                        "Close",
                        modifier = Modifier.clickable { visible = false },
                    )
                }

                Text("Update  is out now")
                Text(
                    style =
                        TextStyle(textDecoration = TextDecoration.Underline, color = Color.Blue),
                    text = "Download on GitHub",
                    modifier = Modifier.clickable { openUri("https://github.com") },
                )
            }
        }
    }
}
