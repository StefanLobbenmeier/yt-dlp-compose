package de.lobbenmeier.stefan.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.rememberDialogState
import dev.nucleusframework.application.DecoratedDialog
import dev.nucleusframework.window.DialogTitleBar
import dev.nucleusframework.window.NucleusDecoratedWindowTheme

@Composable
fun DesktopDialog(
    title: String,
    onClose: () -> Unit,
    width: Dp,
    height: Dp,
    content: @Composable () -> Unit,
) {
    NucleusDecoratedWindowTheme(isDark = !MaterialTheme.colors.isLight) {
        DecoratedDialog(
            onCloseRequest = onClose,
            title = title,
            state = rememberDialogState(width = width, height = height),
            resizable = true,
            onPreviewKeyEvent = {
                if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                    onClose()
                    true
                } else false
            },
        ) {
            Column(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                DialogTitleBar {
                    Text(
                        title,
                        Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.subtitle2,
                    )
                }
                content()
            }
        }
    }
}
