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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.min
import theme.RoadColor

/** Положение машины испытуемого по горизонтали, когда она едет «на месте» (доля ширины, левый край). */
private const val CAR_POSITION = 0.55f

/** Положение светофора без стоп-линии (доля ширины). */
private const val TRAFFIC_LIGHT_POSITION = 0.2f

/** Положение стоп-линии (доля ширины от левого края). */
private const val STOP_LINE_POSITION = 0.15f

/** Высота машины на картинке car_top_side_view.png относительно её длины (картинка квадратная). */
private const val CAR_IMAGE_BODY_HEIGHT = 0.42f

/** Дистанция до впереди идущей машины, в длинах машины (от её заднего бампера до нашего переднего). */
private const val LEAD_CAR_GAP = 0.6f

/** Впереди идущая машина. */
data class LeadCar(val brakeLightsOn: Boolean)

/**
 * Режим движения машины испытуемого к стоп-линии (тест РДО).
 *
 * @param progress 0 — на старте у правого края, 1 — передний бампер на стоп-линии, > 1 — за линией.
 */
data class ApproachToStopLine(val progress: Float)

/**
 * Дорожная сцена (вид сверху), общая для тестов с машиной. Все размеры вычисляются из размеров
 * области, поэтому сцена корректно масштабируется под любое окно. Машина едет справа налево.
 *
 * @param roadScroll смещение разметки в долях её периода (0..1) — ощущение движения, когда машина
 * стоит на месте по горизонтали.
 * @param lampIndex горящая лампа светофора (0 — красный, 2 — зелёный); null — светофора нет.
 * @param leadCar впереди идущая машина; null — её нет.
 * @param approach режим подъезда к стоп-линии; null — машина едет «на месте», стоп-линии нет.
 * @param stimulusKey идентификатор текущего предъявления сигнала; сообщается первая отрисовка
 * красной лампы или включённых стоп-сигналов впереди идущей машины.
 * @param onBrakePress System.nanoTime() нажатия педали тормоза.
 */
@Composable
fun RoadView(
    onBrakePress: (Long) -> Unit,
    modifier: Modifier = Modifier,
    roadScroll: Float = 0f,
    lampIndex: Int? = null,
    leadCar: LeadCar? = null,
    approach: ApproachToStopLine? = null,
    stimulusKey: Any? = null,
    onStimulusDrawn: ((Long) -> Unit)? = null
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sideHeight = maxHeight * 0.28f
        val roadHeight = maxHeight * 0.36f
        val bottomHeight = maxHeight - sideHeight - roadHeight
        val laneHeight = roadHeight / 2

        val carLength = min(laneHeight * 0.65f / CAR_IMAGE_BODY_HEIGHT, maxWidth * 0.14f)
        val carY = (laneHeight - carLength) / 2
        val stopLineX = maxWidth * STOP_LINE_POSITION

        val carX: Dp = if (approach != null) {
            val startX = maxWidth - carLength - maxWidth * 0.02f
            startX - (startX - stopLineX) * approach.progress
        } else {
            maxWidth * CAR_POSITION
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Обочина со светофором
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sideHeight)
                    .background(RoadColor.roadSide)
            ) {
                if (lampIndex != null) {
                    val lampSize = sideHeight / 5.2f
                    val lightX = if (approach != null) stopLineX - lampSize * 1.6f else maxWidth * TRAFFIC_LIGHT_POSITION
                    SmallTrafficLightTopSideView(
                        currentLampIndex = lampIndex,
                        lampSize = lampSize,
                        stimulusKey = stimulusKey,
                        onLampDrawn = onStimulusDrawn,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = lightX)
                    )
                }
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

                    // Стоп-линия поперёк полосы движения
                    if (approach != null) {
                        val stopLineWidth = maxOf(size.width * 0.008f, lineThickness)
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(stopLineX.toPx() - stopLineWidth, 0f),
                            size = Size(stopLineWidth, lane)
                        )
                    }
                }

                if (leadCar != null) {
                    LeadCarView(
                        brakeLightsOn = leadCar.brakeLightsOn,
                        carLength = carLength,
                        x = carX - carLength * (1 + LEAD_CAR_GAP),
                        y = carY,
                        stimulusKey = stimulusKey,
                        onStimulusDrawn = onStimulusDrawn
                    )
                }

                Image(
                    painter = painterResource("car_top_side_view.png"),
                    contentDescription = "Машина испытуемого",
                    modifier = Modifier
                        .size(carLength)
                        .offset { IntOffset(carX.roundToPx(), carY.roundToPx()) }
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

/**
 * Впереди идущая машина. Стоп-сигналы — у заднего бампера (справа, машина едет влево).
 * Выключенные — тёмно-красные, включённые — ярко-красные: так выглядит реальный стоп-сигнал днём.
 */
@Composable
private fun LeadCarView(
    brakeLightsOn: Boolean,
    carLength: Dp,
    x: Dp,
    y: Dp,
    stimulusKey: Any?,
    onStimulusDrawn: ((Long) -> Unit)?
) {
    Box(
        modifier = Modifier
            .size(carLength)
            .offset(x = x, y = y)
    ) {
        Image(
            painter = painterResource("car_top_side_view.png"),
            contentDescription = "Впереди идущая машина",
            modifier = Modifier.fillMaxSize()
        )
        val lightWidth = carLength * 0.06f
        val lightHeight = carLength * 0.09f
        val bodyTop = carLength * (1 - CAR_IMAGE_BODY_HEIGHT) / 2
        val bodyBottom = carLength - bodyTop
        val lightColor = if (brakeLightsOn) Color(0xFFFF1A1A) else Color(0xFF5A0E0E)
        listOf(bodyTop + carLength * 0.02f, bodyBottom - lightHeight - carLength * 0.02f).forEach { lightY ->
            Box(
                modifier = Modifier
                    .offset(x = carLength - lightWidth - carLength * 0.01f, y = lightY)
                    .size(width = lightWidth, height = lightHeight)
                    .clip(RoundedCornerShape(lightWidth / 3))
                    .background(lightColor)
                    .reportFirstDraw(key = if (brakeLightsOn) stimulusKey else null, onDrawn = onStimulusDrawn)
            )
        }
    }
}
