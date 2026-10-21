package testmenu.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Клавиша клавиатуры с подписью; «Пробел» рисуется широкой, как на клавиатуре. */
@Composable
fun KeyCap(label: String, modifier: Modifier = Modifier, height: Dp = 56.dp) {
    val minWidth = if (label.length > 2) height * 4 else height
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = height)
            .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.shapes.medium)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Мышь с выделенной левой кнопкой — ею отвечают в тесте. */
@Composable
fun MouseIllustration(modifier: Modifier = Modifier, height: Dp = 96.dp) {
    val outline = MaterialTheme.colorScheme.outline
    val body = MaterialTheme.colorScheme.surfaceContainerHighest
    val accent = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.size(width = height * 0.62f, height = height)) {
        val stroke = 2.dp.toPx()
        val radius = CornerRadius(size.width / 2, size.width / 2)
        val bodyShape = Path().apply { addRoundRect(RoundRect(Rect(Offset.Zero, size), radius)) }
        drawPath(bodyShape, color = body)
        // Левая кнопка — верхняя левая часть корпуса, обрезанная по его контуру
        val buttonHeight = size.height * 0.42f
        clipPath(bodyShape) {
            drawRect(color = accent, topLeft = Offset.Zero, size = Size(size.width / 2, buttonHeight))
        }
        drawLine(outline, Offset(size.width / 2, 0f), Offset(size.width / 2, buttonHeight), stroke)
        drawLine(outline, Offset(0f, buttonHeight), Offset(size.width, buttonHeight), stroke)
        drawRoundRect(color = outline, cornerRadius = radius, style = Stroke(width = stroke))
    }
}
