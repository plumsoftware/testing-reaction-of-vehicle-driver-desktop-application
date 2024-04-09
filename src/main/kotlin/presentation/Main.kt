package presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import presentation.home.HomePage
import presentation.theme.AppTheme
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    AppTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {

//    val iconImage: BufferedImage = ImageIO.read(javaClass.getResource("test_icon.png"))

    Window(
        onCloseRequest = ::exitApplication,
        icon = BitmapPainter(useResource("test_icon.png", ::loadImageBitmap)),
        title = "Тест на рекацию"
    ) {
        AppTheme {
            HomePage()
        }
    }
}
