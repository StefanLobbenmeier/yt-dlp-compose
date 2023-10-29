import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.lobbenmeier.stefan.ui.App
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.prefs.Preferences

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { Preferences.userRoot().absolutePath() }
    application { Window(onCloseRequest = ::exitApplication) { App() } }
}
