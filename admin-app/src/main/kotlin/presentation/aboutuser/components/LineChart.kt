package presentation.aboutuser.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.regular.userstatistic.LinearGraphItem

@Composable
fun LineChart(data: List<LinearGraphItem>, modifier: Modifier = Modifier.fillMaxSize()) {

//        region::Defining Data Ranges
    val minX = data.minOfOrNull { it.valueX } ?: 0L
    val maxX = data.maxOfOrNull { it.valueX } ?: 0L
    val minY = data.minOfOrNull { it.valueY } ?: 0.0
    val maxY = data.maxOfOrNull { it.valueY } ?: 0.0
//        endregion

    val textMeasurer = rememberTextMeasurer()
    val minXDraw = minX.toString()
    val axisTextStyle = TextStyle(
        fontSize = 12.sp
    )
    val textLayoutResult = remember(minXDraw) {
        textMeasurer.measure(minXDraw, axisTextStyle)
    }

    Canvas(modifier = modifier) {

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
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = horizontalAxisPaint.strokeWidth
        )
        drawLine(
            color = verticalAxisPaint.color,
            start = Offset(0f, size.height),
            end = Offset(0f, 0f),
            strokeWidth = verticalAxisPaint.strokeWidth
        )
//        endregion

//        region::Draw lines
        val linePaint = Paint().apply {
            color = Color.Blue
            strokeWidth = 4f
            style = PaintingStyle.Stroke
        }
//        endregion

//        region::Scaling data for display on Canvas
        val xScale = size.width / (maxX - minX).toFloat()
        val yScale = size.height / (maxY - minY).toFloat()

        var prevPoint: Offset? = null
        data.forEach { (x, y) ->
            val point = Offset((x - minX).toFloat() * xScale, size.height - (y - minY).toFloat() * yScale)
            if (prevPoint != null) {
                drawLine(
                    start = prevPoint!!,
                    end = point,
                    color = linePaint.color,
                    strokeWidth = linePaint.strokeWidth,
                )
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
                y = size.height - 20.dp.toPx()
            )
        )
//        drawText(
//            minX.toString(),
//            0f,
//            ,
//            textPaint
//        )
//        drawText(maxX.toString(), size.width - 50.dp.toPx(), size.height + 16.dp.toPx(), textPaint)
//        drawText(minY.toString(), -20.dp.toPx(), size.height, textPaint)
//        drawText(maxY.toString(), -20.dp.toPx(), 0f, textPaint)
//        endregion
    }
}