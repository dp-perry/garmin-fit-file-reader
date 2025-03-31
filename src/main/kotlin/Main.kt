import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import logic.decodeFitFile
import model.FitSummary
import java.io.File

@Composable
@Preview
fun App() {
    var fitSummary by remember { mutableStateOf<FitSummary?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = {
            val chooser = java.awt.FileDialog(null as java.awt.Frame?, "Select .fit File")
            chooser.isVisible = true
            val file = chooser.file?.let { File(chooser.directory + it) }
            file?.let {
                fitSummary = decodeFitFile(it)
            }
        }) {
            Text("Open .fit File")
        }

        fitSummary?.let { summary ->
            Text("Distance: %.2f mi".format(summary.distanceMiles))
            Text("Average Heart Rate: ${summary.avgHeartRate} bpm")
            Text("Average Cadence: ${summary.avgCadence} rpm")
            Text("Average Pace: %.2f min/mi".format(summary.avgPaceMinPerMile))

            // Add Charts
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "FIT Reader") {
        App()
    }
}