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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.dto.test.TestDTO
import other.components.BackButton
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
                    TestFinishedComponent(result = result, saveStatus = saveStatus)
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
