package other.components.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
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
fun GasPedalButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    val modifier = Modifier
        .onPointerEvent(PointerEventType.Press) {
            isPressed = true
        }
        .onPointerEvent(PointerEventType.Release) {
            isPressed = false
        }
        .scale(if (isPressed) 0.9f else 1f)
        .padding(8.dp)

    IconButton(
        modifier = modifier,
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent
        )
    ) {
        Image(
            painter = painterResource("gas_pedal.png"),
            contentDescription = "Педаль газа"
        )
    }
}
