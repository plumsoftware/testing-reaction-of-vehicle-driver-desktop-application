package other.components.test

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.min
import theme.RoadColor

/** Положение машины по горизонтали (доля ширины, левый край машины). */
private const val CAR_POSITION = 0.55f

/** Положение светофора впереди машины (доля ширины). */
private const val TRAFFIC_LIGHT_POSITION = 0.2f

/** Высота машины на картинке car_top_side_view.png относительно её длины (картинка квадратная). */
private const val CAR_IMAGE_BODY_HEIGHT = 0.42f

/**
 * Дорога с едущей машиной и светофором на обочине. Машина стоит на месте по горизонтали,
 * движение передаётся смещением разметки ([roadScroll]). Все размеры вычисляются из размеров
 * области, поэтому сцена корректно масштабируется под любое окно.
 *
 * @param roadScroll смещение разметки в долях её периода (0..1).
 * @param lampIndex горящая лампа светофора: 0 — красный, 2 — зелёный.
 * @param stimulusKey идентификатор текущего предъявления красного сигнала (для [onStimulusDrawn]).
 * @param onStimulusDrawn момент первой отрисовки красной лампы — момент предъявления сигнала.
 * @param onBrakePress System.nanoTime() нажатия педали тормоза.
 */
@Composable
fun RoadView(
    roadScroll: Float,
    lampIndex: Int,
    onBrakePress: (Long) -> Unit,
    modifier: Modifier = Modifier,
    stimulusKey: Any? = null,
    onStimulusDrawn: ((Long) -> Unit)? = null
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sideHeight = maxHeight * 0.28f
        val roadHeight = maxHeight * 0.36f
        val bottomHeight = maxHeight - sideHeight - roadHeight
        val laneHeight = roadHeight / 2

        val carLength = min(laneHeight * 0.65f / CAR_IMAGE_BODY_HEIGHT, maxWidth * 0.16f)
        val carX = maxWidth * CAR_POSITION
        val carY = (laneHeight - carLength) / 2

        Column(modifier = Modifier.fillMaxSize()) {
            // Обочина со светофором впереди по ходу движения
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sideHeight)
                    .background(RoadColor.roadSide)
            ) {
                val lampSize = sideHeight / 5.2f
                SmallTrafficLightTopSideView(
                    currentLampIndex = lampIndex,
                    lampSize = lampSize,
                    stimulusKey = stimulusKey,
                    onLampDrawn = onStimulusDrawn,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = maxWidth * TRAFFIC_LIGHT_POSITION)
                )
            }

            // Дорога
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(roadHeight)
                    .background(Color.LightGray)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lane = size.height / 2
                    val lineThickness = size.height * 0.03f

                    // Прерывистая разделительная линия; машина едет влево — разметка смещается вправо
                    val dash = size.height * 0.12f
                    val period = dash * 2
                    var x = -period + roadScroll * period
                    while (x < size.width) {
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(x, lane - lineThickness / 2),
                            size = Size(dash, lineThickness)
                        )
                        x += period
                    }
                }

                Image(
                    painter = painterResource("car_top_side_view.png"),
                    contentDescription = "Машина на дороге",
                    modifier = Modifier
                        .size(carLength)
                        .offset(x = carX, y = carY)
                )
            }

            // Обочина с педалью тормоза
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomHeight)
                    .background(RoadColor.roadSide),
                contentAlignment = Alignment.Center
            ) {
                StopPedalButton(height = bottomHeight * 0.85f, onPress = onBrakePress)
            }
        }
    }
}
