package tests.common

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import data.model.dto.test.InputDevice
import data.model.dto.test.TestDTO
import java.io.File
import kotlinx.coroutines.delay
import model.tests.ResponseBinding
import other.components.BackButton
import other.components.ResultLabels
import other.components.SaveStatus
import other.components.TestFinishedComponent

/** Текст над тестом: обратный отсчёт или номер текущей попытки. */
fun testStatusText(phase: TestPhase, countdown: Int, presented: Int, count: Int, isPractice: Boolean): String {
    val prefix = if (isPractice) "Тренировка: " else ""
    val total = if (isPractice) PRACTICE_TRIALS else count
    return if (phase == TestPhase.COUNTDOWN) "${prefix}начало через $countdown".replaceFirstChar { it.uppercase() }
    else "${prefix}попытка ${minOf(presented + 1, total)} из $total".replaceFirstChar { it.uppercase() }
}

/**
 * Каркас экрана теста: кнопка «Назад», итоги после завершения, экран перехода от тренировки
 * к зачётной части, иначе — [content] с ограничениями размеров области для расчёта масштаба.
 */
@Composable
fun TestScreenScaffold(
    phase: TestPhase,
    result: TestDTO,
    saveStatus: SaveStatus,
    resultFiles: List<File>,
    onBack: () -> Unit,
    onStartMainTest: () -> Unit,
    onResponseKey: ResponseKeyHandler? = null,
    keyboardHint: String = "Выбрана клавиатура — отвечайте клавишами, мышь не используется",
    resultLabels: ResultLabels = ResultLabels(),
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val pressedKeys = remember { mutableStateListOf<Key>() }
    // Клик мышью в режиме клавиатуры: подсказка на 2 с, иначе кажется, что тест «не реагирует»
    var showKeyboardHint by remember { mutableStateOf(false) }
    LaunchedEffect(showKeyboardHint) {
        if (showKeyboardHint) {
            delay(2000)
            showKeyboardHint = false
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .responseKeys(
                enabled = phase != TestPhase.FINISHED,
                onResponseKey = onResponseKey,
                pressedKeys = pressedKeys,
                onMousePressed = { showKeyboardHint = true }
            )
    ) {
        if (phase == TestPhase.FINISHED) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp)) {
                    TestFinishedComponent(
                        result = result,
                        saveStatus = saveStatus,
                        resultFiles = resultFiles,
                        labels = resultLabels
                    )
                }
            }
        } else if (phase == TestPhase.PRACTICE_FINISHED) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                PracticeFinishedComponent(onStart = onStartMainTest)
            }
        } else {
            CompositionLocalProvider(LocalPressedResponseKeys provides pressedKeys) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    content = content
                )
            }
        }

        Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
            BackButton(onClick = onBack)
        }

        if (showKeyboardHint && phase != TestPhase.FINISHED) {
            Text(
                text = keyboardHint,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onError,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.error, MaterialTheme.shapes.medium)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    }
}

/** Тренировка окончена: кнопка запуска зачётной части. */
@Composable
private fun PracticeFinishedComponent(onStart: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.widthIn(max = 640.dp).padding(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Тренировка окончена", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Теперь начнётся тест. Его результаты будут записаны. " +
                    "Действуйте так же, как на тренировке: быстро и без ошибок.",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onStart, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Начать тест", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/**
 * Ответ с клавиатуры: получает клавишу и System.nanoTime() нажатия, возвращает true,
 * если клавиша — ответ в этом тесте.
 */
typealias ResponseKeyHandler = (key: Key, pressNanos: Long) -> Boolean

/**
 * Принимает клавиши, пока идёт тест: держит фокус на экране теста и перехватывает нажатия
 * раньше вложенных элементов (иначе Пробел нажал бы кнопку «Назад», если фокус на ней).
 * Автоповтор удерживаемой клавиши не считается новым ответом.
 */
@Composable
private fun Modifier.responseKeys(
    enabled: Boolean,
    onResponseKey: ResponseKeyHandler?,
    pressedKeys: SnapshotStateList<Key>,
    onMousePressed: () -> Unit
): Modifier {
    if (onResponseKey == null) return this
    val currentHandler by rememberUpdatedState(onResponseKey)
    val currentOnMousePressed by rememberUpdatedState(onMousePressed)
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(enabled) {
        pressedKeys.clear()
        if (enabled) focusRequester.requestFocus()
    }
    return this
        // Нажатие мыши не перехватывается (кнопка «Назад» работает), только возвращает фокус клавишам
        .pointerInput(enabled) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (enabled && event.type == PointerEventType.Press) {
                        focusRequester.requestFocus()
                        currentOnMousePressed()
                    }
                }
            }
        }
        // Обработчик клавиш должен стоять ДО focusable(): события клавиатуры получают только
        // модификаторы, оборачивающие элемент с фокусом
        .onPreviewKeyEvent { event ->
            val pressNanos = System.nanoTime()
            if (!enabled) return@onPreviewKeyEvent false
            when (event.type) {
                KeyEventType.KeyDown -> {
                    if (event.key in pressedKeys) return@onPreviewKeyEvent true
                    pressedKeys.add(event.key)
                    currentHandler(event.key, pressNanos)
                }

                KeyEventType.KeyUp -> {
                    pressedKeys.remove(event.key)
                    false
                }

                else -> false
            }
        }
        .focusRequester(focusRequester)
        .focusable()
}

/** Клавиши, которые сейчас удерживаются, — чтобы показать педаль нажатой. */
val LocalPressedResponseKeys = compositionLocalOf<List<Key>> { emptyList() }

/** Обработчик ответа: при ответе с клавиатуры — клавиша, иначе null (отвечают мышью). */
fun keyboardResponses(keyboard: Boolean, bindings: Map<Key, (Long) -> Unit>): ResponseKeyHandler? =
    if (!keyboard) null else { key, pressNanos ->
        val onPress = bindings[key]
        onPress?.invoke(pressNanos)
        onPress != null
    }

/** Испытуемый отвечает с клавиатуры (выбрано в окне перед запуском теста). */
val TestDTO.respondsWithKeyboard: Boolean
    get() = preTestState.inputDevice == InputDevice.KEYBOARD

/** Подсказка над сценой: «нажмите «Пробел»» или «нажмите педаль тормоза». */
fun responseHint(keyboard: Boolean, binding: ResponseBinding): String =
    if (keyboard) "нажмите «${binding.keyLabel}»" else "нажмите ${binding.mouseTarget}"

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
