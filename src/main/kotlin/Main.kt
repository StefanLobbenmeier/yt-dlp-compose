import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import de.lobbenmeier.stefan.ui.App
import java.awt.Button
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.Label

fun main() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        Dialog(Frame(), e.message ?: "Error").apply {
            layout = FlowLayout()
            add(Label(e.message))
            add(Label(e.stackTraceToString()))
            val button = Button("OK").apply { addActionListener { dispose() } }
            add(button)
            setSize(300, 300)
            isVisible = true
        }
    }

    singleWindowApplication(
        title = "Open Video Downloader v3",
        state = WindowState(width = 800.dp, height = 800.dp),
        alwaysOnTop = false,
    ) {
        App()
    }
}
