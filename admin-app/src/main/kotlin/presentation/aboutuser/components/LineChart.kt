package presentation.aboutuser.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.regular.userstatistic.LineChartItem
import presentation.theme.ExtendedTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun LineChart(data: List<LineChartItem>, modifier: Modifier = Modifier.fillMaxSize()) {

    val heightOffset = 20f
    val widthOffset = 20f

    var xScale: Float
    var yScale: Float

//        region::Defining Data Ranges
    val minX = data.minOfOrNull { it.valueX } ?: 0L
    val maxX = data.maxOfOrNull { it.valueX } ?: 0L
    val minY = data.minOfOrNull { it.valueY } ?: 0.0
    val maxY = data.maxOfOrNull { it.valueY } ?: 0.0
//        endregion


//        region::Draw lines
    val linePaint = Paint().apply {
        color = ExtendedTheme.colors.lineChartColor
        strokeWidth = 4f
        style = PaintingStyle.Stroke
    }
    val pointPaint = Paint().apply {
        color = ExtendedTheme.colors.pointChartColor
        style = PaintingStyle.Fill
    }
//        endregion

    val textMeasurer = rememberTextMeasurer()

    val minXDraw = SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date(minX))
    val maxXDraw = SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date(maxX))

    val minYDraw = minY.toString()
    val maxYDraw = maxY.toString()

    val axisTextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ExtendedTheme.colors.lineChartAxisColor
    )

    val textLayoutResult = remember(minXDraw) {
        textMeasurer.measure(minXDraw, axisTextStyle)
    }

    var hoveredPoint by remember { mutableStateOf<Offset?>(null) }
    var hoveredCoordinates by remember { mutableStateOf<Pair<Long, Double>?>(null) }

    Canvas(modifier = modifier
        .pointerInput(Unit) {

            xScale = size.width / (maxX - minX).toFloat()
            yScale = size.height / (maxY - minY).toFloat()

            detectTapGestures { offset ->
//                Detect if the cursor is close to any point
                hoveredPoint = data
                    .map { (x, y) ->
                        Offset(
                            (x - minX).toFloat() * xScale,
                            size.height - heightOffset - (y - minY).toFloat() * yScale
                        )
                    }
                    .minByOrNull {
                        it.distanceTo(offset)
                    }
            }

            detectDragGestures { offset ->
//                Detect if the cursor is close to any point
                hoveredPoint = data
                    .map { (x, y) ->
                        Offset(
                            (x - minX).toFloat() * xScale,
                            size.height - heightOffset - (y - minY).toFloat() * yScale
                        )
                    }
                    .minByOrNull {
                        it.distanceTo(offset)
                    }
            }
        }
    ) {

//        region::Axis setup
        val horizontalAxisPaint = Paint().apply {
            color = Color.Gray
            strokeWidth = 2f
        }
        val verticalAxisPaint = Paint().apply {
            color = Color.Gray
            strokeWidth = 2f
        }
//        endregion

//        region::Draw axes
        drawLine(
            color = horizontalAxisPaint.color,
            start = Offset(0f, size.height - heightOffset),
            end = Offset(size.width, size.height - heightOffset),
            strokeWidth = horizontalAxisPaint.strokeWidth
        )
        drawLine(
            color = verticalAxisPaint.color,
            start = Offset(0f, size.height - heightOffset),
            end = Offset(0f, 0f),
            strokeWidth = verticalAxisPaint.strokeWidth
        )
//        endregion

//        region::Scaling data for display on Canvas
        xScale = size.width / (maxX - minX).toFloat()
        yScale = size.height / (maxY - minY).toFloat()

        var prevPoint: Offset? = null
        data.forEach { (x, y) ->
            val point =
                Offset((x - minX).toFloat() * xScale, size.height - heightOffset - (y - minY).toFloat() * yScale)
            if (prevPoint != null) {
                drawLine(
                    start = prevPoint!!,
                    end = point,
                    color = linePaint.color,
                    strokeWidth = linePaint.strokeWidth,
                )
            }

//            Draw point
            drawCircle(
                center = point,
                radius = 5f,
                color = pointPaint.color
            )

//            Draw coordinates below the point
            drawText(
                textMeasurer = textMeasurer,
                text = SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date(x)),
                style = axisTextStyle,
                topLeft = Offset(point.x - widthOffset, size.height - (heightOffset * 2).dp.toPx())
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "$y",
                style = axisTextStyle,
                topLeft = Offset(-widthOffset.dp.toPx(), point.y)
            )

//            Check if the point is hovered
            if (hoveredPoint != null && point.distanceTo(hoveredPoint!!) < 10f) {
                hoveredCoordinates = Pair(x, y)
            }
            prevPoint = point
        }

//        endregion

//        region::Axes naming
        drawText(
            textMeasurer = textMeasurer,
            text = minXDraw,
            style = axisTextStyle,
            topLeft = Offset(
                x = 0f,
                y = size.height - heightOffset.dp.toPx()
            )
        )
        drawText(
            textMeasurer = textMeasurer,
            text = maxXDraw,
            style = axisTextStyle,
            topLeft = Offset(
                x = size.width - heightOffset.dp.toPx(),
                y = size.height - heightOffset.dp.toPx()
            )
        )
        drawText(
            textMeasurer = textMeasurer,
            text = minYDraw,
            style = axisTextStyle,
            topLeft = Offset(
                x = -widthOffset.dp.toPx(),
                y = size.height - (heightOffset * 2).dp.toPx()
            )
        )
        drawText(
            textMeasurer = textMeasurer,
            text = maxYDraw,
            style = axisTextStyle,
            topLeft = Offset(
                x = -widthOffset.dp.toPx(),
                y = 0f
            )
        )
//        endregion

//        Draw hovered coordinates
        hoveredCoordinates?.let { (x, y) ->
            drawText(
                textMeasurer = textMeasurer,
                text = "(${SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date(x))}, $y)",
                style = axisTextStyle.copy(color = Color.Black),
                topLeft = hoveredPoint!!.copy(y = hoveredPoint!!.y - heightOffset.dp.toPx())
            )
        }
    }
}

private fun Offset.distanceTo(other: Offset): Float {
    return sqrt((x - other.x).pow(2) + (y - other.y).pow(2))
}