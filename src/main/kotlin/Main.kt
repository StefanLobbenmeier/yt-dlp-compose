import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.lobbenmeier.stefan.ui.App

fun main() = application { Window(onCloseRequest = ::exitApplication) { App() } }
