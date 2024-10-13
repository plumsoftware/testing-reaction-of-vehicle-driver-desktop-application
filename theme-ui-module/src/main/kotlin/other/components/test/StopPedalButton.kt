package other.components.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StopPedalButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    var isPressed by remember { mutableStateOf(false) }

    val modifier_ = Modifier
        .size(width = 200.dp, height = 310.dp)
        .onPointerEvent(PointerEventType.Press) {
            isPressed = true
            onClick.invoke()
        }
        .onPointerEvent(PointerEventType.Release) {
            isPressed = false
        }
        .scale(if (isPressed) 0.8f else 1f)
        .padding(8.dp)
        .then(modifier)

    IconButton(
        modifier = modifier_,
        onClick = {},
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent
        )
    ) {
        Image(
            painter = painterResource("stop_pedal.png"),
            contentDescription = "Педаль тормаза"
        )
    }
}