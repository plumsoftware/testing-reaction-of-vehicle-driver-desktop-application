package tests.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.dto.test.TestDTO
import other.components.BackButton
import other.components.ResultLabels
import other.components.SaveStatus
import other.components.TestFinishedComponent

/** Текст над тестом: обратный отсчёт или номер текущей попытки. */
fun testStatusText(phase: TestPhase, countdown: Int, presented: Int, count: Int): String =
    if (phase == TestPhase.COUNTDOWN) "Начало через $countdown"
    else "Попытка ${minOf(presented + 1, count)} из $count"

/**
 * Каркас экрана теста: кнопка «Назад», итоги после завершения, иначе — [content]
 * с ограничениями размеров области для расчёта масштаба.
 */
@Composable
fun TestScreenScaffold(
    phase: TestPhase,
    result: TestDTO,
    saveStatus: SaveStatus,
    onBack: () -> Unit,
    resultLabels: ResultLabels = ResultLabels(),
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (phase == TestPhase.FINISHED) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp)) {
                    TestFinishedComponent(result = result, saveStatus = saveStatus, labels = resultLabels)
                }
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                content = content
            )
        }

        Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
            BackButton(onClick = onBack)
        }
    }
}

/** Стиль текста статуса над тестом. */
@Composable
fun statusTextStyle() = MaterialTheme.typography.headlineMedium

/** Скорость движения разметки, периодов разметки в секунду (только визуально). */
private const val ROAD_SCROLL_PERIODS_PER_SECOND = 4f

/**
 * Смещение дорожной разметки для [other.components.test.RoadView], пока машина едет.
 * Считается от времени кадра: скорость одинакова при любой частоте кадров.
 */
@Composable
fun rememberRoadScroll(isDriving: Boolean): Float {
    var roadScroll by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isDriving) {
        if (!isDriving) return@LaunchedEffect
        var lastFrameNanos = withFrameNanos { it }
        while (true) {
            withFrameNanos { frameNanos ->
                val seconds = (frameNanos - lastFrameNanos) / 1_000_000_000f
                roadScroll = (roadScroll + seconds * ROAD_SCROLL_PERIODS_PER_SECOND) % 1f
                lastFrameNanos = frameNanos
            }
        }
    }
    return roadScroll
}

/** Подсказка над дорожной сценой: статус и инструкция. */
@Composable
fun RoadSceneHeader(status: String, instruction: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = status,
            style = statusTextStyle().copy(color = androidx.compose.ui.graphics.Color.Black)
        )
        androidx.compose.material3.Text(
            text = instruction,
            style = MaterialTheme.typography.bodyMedium.copy(color = androidx.compose.ui.graphics.Color.Black)
        )
    }
}
