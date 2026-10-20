package other.components.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import theme.trafficLightBranchColor
import theme.trafficLightColor

/** Цвета ламп по индексу: 0 — красный, 1 — жёлтый, 2 — зелёный. */
val trafficLightLampColors = listOf(Color.Red, Color.Yellow, Color.Green)

/** Высота светофора (корпус + стойка) в долях размера лампы — для расчёта размера под экран. */
const val TRAFFIC_LIGHT_HEIGHT_IN_LAMPS = 5.9f

/**
 * Светофор, все размеры которого пропорциональны [lampSize].
 *
 * @param currentLampIndex горящая лампа (0..2) или -1, если все погашены.
 * @param stimulusKey идентификатор текущего предъявления сигнала (для [onLampDrawn]).
 * @param onLampDrawn вызывается с моментом первой отрисовки горящей лампы.
 */
@Composable
fun TrafficLight(
    currentLampIndex: Int,
    lampSize: Dp,
    modifier: Modifier = Modifier,
    stimulusKey: Any? = null,
    onLampDrawn: ((Long) -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(lampSize * 0.3f),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(lampSize * 0.3f))
                .background(trafficLightColor)
                .padding(horizontal = lampSize * 0.45f, vertical = lampSize * 0.35f)
        ) {
            trafficLightLampColors.forEachIndexed { index, color ->
                val isLit = index == currentLampIndex
                Box(
                    modifier = Modifier
                        .size(lampSize)
                        .clip(CircleShape)
                        .background(if (isLit) color else Color.Gray)
                        .reportFirstDraw(
                            key = if (isLit) stimulusKey else null,
                            onDrawn = onLampDrawn
                        )
                )
            }
        }
        Box(
            modifier = Modifier
                .width(lampSize * 0.5f)
                .height(lampSize)
                .background(trafficLightBranchColor)
        )
    }
}

/**
 * Компактный светофор на обочине в тесте с машиной.
 *
 * @param stimulusKey идентификатор текущего предъявления сигнала (для [onLampDrawn]).
 * @param onLampDrawn вызывается с моментом первой отрисовки горящей лампы.
 */
@Composable
fun SmallTrafficLightTopSideView(
    currentLampIndex: Int,
    lampSize: Dp,
    modifier: Modifier = Modifier,
    stimulusKey: Any? = null,
    onLampDrawn: ((Long) -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(lampSize * 0.12f),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(lampSize * 0.25f))
                .background(trafficLightColor)
                .padding(lampSize * 0.24f)
        ) {
            trafficLightLampColors.forEachIndexed { index, color ->
                val isLit = index == currentLampIndex
                Box(
                    modifier = Modifier
                        .size(lampSize)
                        .clip(CircleShape)
                        .background(if (isLit) color else Color.Gray)
                        .reportFirstDraw(
                            key = if (isLit) stimulusKey else null,
                            onDrawn = onLampDrawn
                        )
                )
            }
        }
        Box(
            modifier = Modifier
                .width(lampSize * 0.4f)
                .height(lampSize)
                .background(trafficLightBranchColor)
        )
    }
}
