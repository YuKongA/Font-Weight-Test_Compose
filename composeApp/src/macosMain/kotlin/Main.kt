import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import fontweighttest.composeapp.generated.resources.Res
import fontweighttest.composeapp.generated.resources.app_name
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.compose.resources.stringResource
import platform.AppKit.NSApplication
import platform.AppKit.NSApplicationActivationPolicy
import platform.AppKit.NSApplicationDelegateProtocol
import platform.CoreGraphics.CGSizeMake
import platform.darwin.NSObject
import top.yukonga.fontWeightTest.App

@OptIn(ExperimentalForeignApi::class)
fun main() {
    val nsApplication = NSApplication.sharedApplication()
    nsApplication.setActivationPolicy(NSApplicationActivationPolicy.NSApplicationActivationPolicyRegular)
    nsApplication.delegate =
        object : NSObject(), NSApplicationDelegateProtocol {
            override fun applicationShouldTerminateAfterLastWindowClosed(sender: NSApplication): Boolean = true
        }
    Window(
        size = DpSize(420.dp, 840.dp),
    ) {
        window.title = stringResource(Res.string.app_name)
        window.minSize = CGSizeMake(300.0, 600.0)
        App()
    }
    nsApplication.run()
}