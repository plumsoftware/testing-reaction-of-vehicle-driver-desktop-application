package other.components.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

/** Отношение ширины к высоте у изображений педалей (исходный размер области 200 × 310). */
const val PEDAL_ASPECT_RATIO = 200f / 310f

/**
 * Педаль, срабатывающая в момент нажатия кнопки мыши.
 *
 * @param height высота педали; ширина вычисляется по [PEDAL_ASPECT_RATIO].
 * @param onPress получает System.nanoTime() нажатия; null — мышь не используется (ответ с клавиатуры).
 * @param pressed педаль нажата клавишей — показывается нажатой.
 */
@Composable
private fun PedalButton(
    resource: String,
    contentDescription: String,
    height: Dp,
    onPress: ((Long) -> Unit)?,
    pressed: Boolean,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val pointer = if (onPress != null) {
        Modifier.onPressTimestamp(onPressedChange = { isPressed = it }, onPress = onPress)
    } else {
        Modifier
    }

    Image(
        painter = painterResource(resource),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .height(height)
            .width(height * PEDAL_ASPECT_RATIO)
            .then(pointer)
            .scale(if (isPressed || pressed) 0.85f else 1f)
    )
}

@Composable
fun GasPedalButton(
    height: Dp,
    onPress: ((Long) -> Unit)?,
    modifier: Modifier = Modifier,
    pressed: Boolean = false
) {
    PedalButton(
        resource = "gas_pedal.png",
        contentDescription = "Педаль газа",
        height = height,
        onPress = onPress,
        pressed = pressed,
        modifier = modifier
    )
}

@Composable
fun StopPedalButton(
    height: Dp,
    onPress: ((Long) -> Unit)?,
    modifier: Modifier = Modifier,
    pressed: Boolean = false
) {
    PedalButton(
        resource = "stop_pedal.png",
        contentDescription = "Педаль тормоза",
        height = height,
        onPress = onPress,
        pressed = pressed,
        modifier = modifier
    )
}
