import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.app_icon
import fontweighttest.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(420.dp, 840.dp),
        position = WindowPosition.Aligned(Alignment.Center)
    )
    Window(
        state = state,
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.app_icon),
    ) {
        App()
    }
}