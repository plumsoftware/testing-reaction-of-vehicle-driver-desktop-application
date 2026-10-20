package other.components.test

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.input.pointer.pointerInput

/*
 * Все отметки времени в тестах берутся из System.nanoTime() — монотонных часов JVM.
 * System.currentTimeMillis() для измерения интервалов не подходит: он может скачком
 * меняться при синхронизации времени ОС.
 */

/**
 * Вызывает [onPress] в момент НАЖАТИЯ кнопки мыши (а не отпускания, как Button.onClick),
 * передавая System.nanoTime() этого события.
 *
 * @param onPressedChange для визуальной обратной связи (нажато/отпущено).
 */
@Composable
fun Modifier.onPressTimestamp(
    onPressedChange: (Boolean) -> Unit = {},
    onPress: (pressNanos: Long) -> Unit
): Modifier {
    val currentOnPress by rememberUpdatedState(onPress)
    val currentOnPressedChange by rememberUpdatedState(onPressedChange)
    return pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            val pressNanos = System.nanoTime()
            currentOnPressedChange(true)
            currentOnPress(pressNanos)
            waitForUpOrCancellation()
            currentOnPressedChange(false)
        }
    }
}

/**
 * Сообщает System.nanoTime() момента, когда элемент впервые отрисован для данного [key].
 * Используется как момент предъявления сигнала: это ближайшая к реальному появлению
 * на экране точка, доступная программе (задержку самого монитора программа измерить не может).
 *
 * @param key идентификатор предъявления сигнала; null — ничего не сообщать.
 */
@Composable
fun Modifier.reportFirstDraw(key: Any?, onDrawn: ((drawNanos: Long) -> Unit)?): Modifier {
    if (key == null || onDrawn == null) return this
    val currentOnDrawn by rememberUpdatedState(onDrawn)
    val reported = remember(key) { BooleanArray(1) }
    return drawWithContent {
        drawContent()
        if (!reported[0]) {
            reported[0] = true
            currentOnDrawn(System.nanoTime())
        }
    }
}
