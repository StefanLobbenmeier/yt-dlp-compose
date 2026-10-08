package de.lobbenmeier.stefan.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.settings.business.Appearance
import de.lobbenmeier.stefan.ui.AppTheme
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.nucleusApplication
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

/** Real Tao-window regression check; run with desktopDialogSmoke on a graphical desktop. */
fun main() =
    nucleusApplication(backend = NucleusBackend.Tao) {
        val dialogs = remember { DesktopDialogController() }
        val opened = remember { AtomicInteger() }
        val done = remember { CompletableDeferred<Unit>() }
        CompositionLocalProvider(LocalDesktopDialogs provides dialogs) {
            DesktopDialogs(dialogs)
            for (appearance in listOf(Appearance.LIGHT, Appearance.DARK)) {
                AppTheme(appearance) {
                    DecoratedWindow(
                        onCloseRequest = ::exitApplication,
                        title = "Dialog regression: $appearance",
                    ) {
                        AppTheme(appearance) {
                            // Reproduce the original source: a button under Scaffold's
                            // subcomposition,
                            // with white button content that must not leak into the new window.
                            Scaffold {
                                CompositionLocalProvider(LocalContentColor provides Color.White) {
                                    SmokeButton {
                                        if (opened.incrementAndGet() == 2) done.complete(Unit)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            withTimeout(10000) { done.await() }
            println(
                "PASS: one request opens each Tao dialog without another input event; light/dark content colors are correct"
            )
            exitApplication()
        }
    }

@Composable
private fun SmokeButton(onOpened: () -> Unit) {
    val open =
        rememberDesktopDialogLauncher("Settings smoke", 480.dp, 480.dp) {
            val contentColor = LocalContentColor.current
            val expectedColor = MaterialTheme.colors.onBackground
            LaunchedEffect(Unit) {
                check(contentColor == expectedColor) {
                    "Dialog inherited the opening button's content color"
                }
                onOpened()
            }
            DesktopScrollableColumn { repeat(60) { Text("Settings row $it") } }
        }
    Column {
        Button(onClick = open) { Text("Open Settings") }
        LaunchedEffect(Unit) {
            delay(300)
            // Invoke the exact button callback once. No second click or periodic redraw.
            open()
        }
    }
}
