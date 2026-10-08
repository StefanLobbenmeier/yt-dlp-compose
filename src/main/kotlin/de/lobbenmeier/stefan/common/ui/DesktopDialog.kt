package de.lobbenmeier.stefan.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
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
import kotlinx.coroutines.channels.Channel

class DesktopDialogController {
    internal val dialogs = mutableStateListOf<DesktopDialogRequest>()
    private val requests = Channel<DialogAction>(Channel.UNLIMITED)

    internal fun open(request: DesktopDialogRequest) {
        requests.trySend(DialogAction.Open(request)).getOrThrow()
    }

    internal fun close(id: Any) {
        requests.trySend(DialogAction.Close(id)).getOrThrow()
    }

    internal suspend fun processRequests() {
        for (action in requests) {
            when (action) {
                is DialogAction.Open ->
                    if (dialogs.none { it.id === action.request.id }) dialogs.add(action.request)
                is DialogAction.Close -> dialogs.removeAll { it.id === action.id }
            }
        }
    }

    private sealed interface DialogAction {
        data class Open(val request: DesktopDialogRequest) : DialogAction

        data class Close(val id: Any) : DialogAction
    }
}

internal class DesktopDialogRequest(
    val id: Any,
    val title: String,
    val width: Dp,
    val height: Dp,
    val locals: CompositionLocalContext,
    val content: @Composable (() -> Unit) -> Unit,
)

val LocalDesktopDialogs =
    staticCompositionLocalOf<DesktopDialogController> {
        error("Desktop dialogs must be hosted by the application")
    }

/** Requests a window directly from the click handler, outside layout subcompositions. */
@Composable
fun rememberDesktopDialogLauncher(
    title: String,
    width: Dp,
    height: Dp,
    content: @Composable (() -> Unit) -> Unit,
): () -> Unit {
    val controller = LocalDesktopDialogs.current
    val id = remember { Any() }
    val locals = currentCompositionLocalContext
    val latestContent = rememberUpdatedState(content)
    DisposableEffect(controller, id) { onDispose { controller.close(id) } }
    return {
        controller.open(
            DesktopDialogRequest(id, title, width, height, locals) { close ->
                latestContent.value(close)
            }
        )
    }
}

/** Compose this beside the main window in nucleusApplication, rather than inside a layout. */
@Composable
fun DesktopDialogs(controller: DesktopDialogController) {
    // Each Tao window has its own scene/recomposer. Pointer callbacks must wake
    // the application host independently of the originating scene's frame snapshot.
    LaunchedEffect(controller) { controller.processRequests() }
    controller.dialogs.forEach { request ->
        key(request.id) {
            // Preserve the owning window for native parent/modality handling.
            CompositionLocalProvider(request.locals) {
                val colors = MaterialTheme.colors.copy()
                val typography = MaterialTheme.typography
                val shapes = MaterialTheme.shapes
                val close = { controller.close(request.id) }
                NucleusDecoratedWindowTheme(isDark = !colors.isLight) {
                    DecoratedDialog(
                        onCloseRequest = close,
                        title = request.title,
                        state = rememberDialogState(width = request.width, height = request.height),
                        resizable = true,
                        onPreviewKeyEvent = {
                            if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                                close()
                                true
                            } else false
                        },
                    ) {
                        // A new scene needs its own theme. Surface resets content colors inherited
                        // from the opening button (onPrimary is often white in a light theme).
                        MaterialTheme(colors = colors, typography = typography, shapes = shapes) {
                            NucleusDecoratedWindowTheme(isDark = !colors.isLight) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = colors.background,
                                    contentColor = colors.onBackground,
                                ) {
                                    Column(Modifier.fillMaxSize()) {
                                        DialogTitleBar {
                                            Text(
                                                request.title,
                                                Modifier.align(Alignment.CenterHorizontally),
                                                color = colors.onSurface,
                                                style = typography.subtitle2,
                                            )
                                        }
                                        request.content(close)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
