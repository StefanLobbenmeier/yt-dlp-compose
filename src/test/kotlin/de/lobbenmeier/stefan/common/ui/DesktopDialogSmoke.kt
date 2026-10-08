package de.lobbenmeier.stefan.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import de.lobbenmeier.stefan.downloadlist.ui.Header
import de.lobbenmeier.stefan.settings.business.Appearance
import de.lobbenmeier.stefan.settings.business.createEmptySettings
import de.lobbenmeier.stefan.settings.ui.SettingsUI
import de.lobbenmeier.stefan.ui.AppTheme
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.NucleusWindow
import dev.nucleusframework.application.nucleusApplication
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/** Real Tao-window regression check; run with desktopDialogSmoke on a graphical desktop. */
fun main() =
    nucleusApplication(backend = NucleusBackend.Tao, enableSingleInstance = false) {
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
                                    SmokeButton(nucleusWindow) {
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
            // A stalled native loop cannot service a coroutine timeout.
            thread(name = "DialogSmokeWatchdog", isDaemon = true) {
                Thread.sleep(20000)
                System.err.println("FAIL: Settings did not open after one pointer click")
                Runtime.getRuntime().halt(1)
            }
            withTimeout(12000) { done.await() }
            println(
                "PASS: one pointer click opens each Tao dialog without another input event; light/dark content colors are correct"
            )
            exitApplication()
        }
    }

@Composable
private fun SmokeButton(window: NucleusWindow, onOpened: () -> Unit) {
    val settings = remember { createEmptySettings() }
    val target = remember { arrayOf(Offset.Unspecified) }
    val clicked = remember { AtomicInteger() }
    val pendingClickSnapshot = remember { Snapshot.takeMutableSnapshot() }
    DisposableEffect(pendingClickSnapshot) { onDispose { pendingClickSnapshot.dispose() } }
    val open =
        rememberDesktopDialogLauncher("Settings smoke", 480.dp, 720.dp) { close ->
            val contentColor = LocalContentColor.current
            val expectedColor = MaterialTheme.colors.onBackground
            LaunchedEffect(Unit) {
                check(contentColor == expectedColor) {
                    "Dialog inherited the opening button's content color"
                }
                check(clicked.get() == 1) { "Settings did not receive exactly one pointer click" }
                onOpened()
            }
            SettingsUI(settings, {}, close)
        }
    Box(
        Modifier.onGloballyPositioned {
            val bounds = it.boundsInWindow()
            // The Settings control is the square at the right edge of Header.
            target[0] = Offset(bounds.right - 24f * it.size.height / 64f, bounds.center.y)
        }
    ) {
        Header(
            settings,
            {},
            {
                clicked.incrementAndGet()
                // The dialog host must receive the event even before the caller's
                // scene snapshot is committed. No extra input applies this snapshot.
                pendingClickSnapshot.enter { open() }
            },
        )
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            delay(5000)
            // Feed the native host's real pointer listeners, rather than calling open().
            // Reflection is confined to this test because Tao does not expose input injection.
            val tao =
                window.javaClass
                    .getDeclaredField("taoWindow")
                    .apply { isAccessible = true }
                    .get(window)
            @Suppress("UNCHECKED_CAST")
            val move =
                tao.javaClass
                    .getDeclaredField("pointerMoveListener")
                    .apply { isAccessible = true }
                    .get(tao) as (Int, Int) -> Unit
            @Suppress("UNCHECKED_CAST")
            val button =
                tao.javaClass
                    .getDeclaredField("pointerButtonListener")
                    .apply { isAccessible = true }
                    .get(tao) as (Int, Boolean) -> Unit
            val point = target[0]
            check(point != Offset.Unspecified)
            withContext(Dispatchers.Main) {
                move((point.x * 1024).toInt(), (point.y * 1024).toInt())
                button(0, true)
            }
            delay(50)
            withContext(Dispatchers.Main) { button(0, false) }
            println("Pointer Settings callback count: ${clicked.get()}")
        }
    }
}
