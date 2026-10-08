import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import de.lobbenmeier.stefan.common.ui.DesktopDialogController
import de.lobbenmeier.stefan.common.ui.DesktopDialogs
import de.lobbenmeier.stefan.common.ui.LocalDesktopDialogs
import de.lobbenmeier.stefan.settings.business.SettingsViewModel
import de.lobbenmeier.stefan.ui.App
import de.lobbenmeier.stefan.ui.AppTheme
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.window.TitleBar
import dev.nucleusframework.window.WindowAppearance
import dev.nucleusframework.window.WindowAppearanceMode
import dev.nucleusframework.window.WindowBackground

fun main() =
    nucleusApplication(backend = NucleusBackend.Tao) {
        val settingsViewModel = remember { SettingsViewModel() }
        val settings by settingsViewModel.settings.collectAsState()

        val dialogs = remember { DesktopDialogController() }
        CompositionLocalProvider(LocalDesktopDialogs provides dialogs) {
            AppTheme(settings.appearance) {
                DesktopDialogs(dialogs)
                DecoratedWindow(
                    onCloseRequest = ::exitApplication,
                    title = "Open Video Downloader",
                    state = rememberWindowState(width = 960.dp, height = 800.dp),
                    minimumSize = DpSize(720.dp, 540.dp),
                    nativeContextMenu = true,
                ) {
                    AppTheme(settings.appearance) {
                        WindowBackground(MaterialTheme.colors.background)
                        WindowAppearance(
                            if (MaterialTheme.colors.isLight) WindowAppearanceMode.Light
                            else WindowAppearanceMode.Dark
                        )
                        Column(Modifier.fillMaxSize()) {
                            TitleBar {
                                Text(
                                    title,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    color = MaterialTheme.colors.onSurface,
                                    style = MaterialTheme.typography.subtitle2,
                                )
                            }
                            App(settingsViewModel)
                        }
                    }
                }
            }
        }
    }
