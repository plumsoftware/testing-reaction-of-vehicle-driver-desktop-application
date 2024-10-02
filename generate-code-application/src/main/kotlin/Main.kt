import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import generate_code.GenerateCodePage
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import other.extension.route.DesktopRouting
import theme.AppTheme

fun main() = run {

    println("App started!")

    application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("main_icon.png", ::loadImageBitmap)),
            title = "Генерация уникального кода",
            state = windowState,
        ) {
            PreComposeApp {
                val navigator = rememberNavigator()

                AppTheme(useDarkTheme = true) {
                    NavHost(
                        navigator = navigator,
                        initialRoute = DesktopRouting.generateCode,
                        navTransition = NavTransition(),
                    ) {
                        scene(route = DesktopRouting.generateCode) {
                            println("Generate code page rendered")
                            GenerateCodePage()
                        }
                    }
                }
            }
        }
    }
}