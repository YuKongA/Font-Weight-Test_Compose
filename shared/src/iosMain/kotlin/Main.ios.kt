import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import top.yukonga.fontWeightTest.App

@OptIn(ExperimentalComposeUiApi::class)
@Suppress("unused")
fun main(): UIViewController = ComposeUIViewController(
    configure = {
        parallelRendering = true
    }
) {
    ResourceEnvironmentFix {
        App()
    }
}