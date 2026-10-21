package tests.rmo_stop_line

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import data.model.regular.settings.workbookFiles
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import model.tests.ResponseKeys
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.ResultLabels
import other.components.test.ApproachToStopLine
import other.components.test.RoadView
import tests.common.RoadSceneHeader
import tests.common.TestPhase
import tests.common.LocalPressedResponseKeys
import tests.common.TestScreenScaffold
import tests.common.keyboardResponses
import tests.common.respondsWithKeyboard
import tests.common.responseHint
import tests.common.testStatusText
import tests.rmo_stop_line.store.Effect
import tests.rmo_stop_line.store.Event
import tests.rmo_stop_line.viewmodel.RmoStopLineViewModel

/** Насколько далеко за линию доезжает машина при пропуске (в долях пути до линии). */
private const val MAX_PROGRESS = 1.6f

@Composable
fun RmoStopLine(
    settings: Settings,
    workBookStorage: WorkbookStorage,
    testDTO: TestDTO,
    sessionStorage: SessionStorage,
    navigator: Navigator
) {
    val viewModel: RmoStopLineViewModel =
        viewModel(modelClass = RmoStopLineViewModel::class) {
            RmoStopLineViewModel(
                settings = settings,
                testDTO = testDTO,
                workBookStorage = workBookStorage,
                sessionStorage = sessionStorage
            )
        }

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(Event.StartInitTimer)
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackClicked -> navigator.goBack()
            }
        }
    }

    val state by viewModel.state.collectAsState()

    // Положение машины вычисляется от того же System.nanoTime(), что и ошибка нажатия:
    // на экране и в расчёте машина пересекает линию в один и тот же момент программы
    val motionStart = state.motionStartNanos
    val isMoving = motionStart != null && state.stoppedProgress == null
    var movingProgress by remember(motionStart) { mutableFloatStateOf(0f) }
    LaunchedEffect(motionStart, isMoving) {
        if (!isMoving || motionStart == null) return@LaunchedEffect
        while (movingProgress < MAX_PROGRESS) {
            withFrameNanos {
                movingProgress = ((System.nanoTime() - motionStart) / 1_000_000f / state.travelTimeMs)
                    .coerceIn(0f, MAX_PROGRESS)
            }
        }
    }

    val progress = when {
        motionStart == null -> 0f
        state.stoppedProgress != null -> state.stoppedProgress!!
        else -> movingProgress
    }

    val keyboard = state.testDTO.respondsWithKeyboard

    TestScreenScaffold(
        phase = state.phase,
        result = state.testDTO,
        saveStatus = state.saveStatus,
        resultFiles = settings.workbookFiles,
        onBack = { viewModel.onEvent(Event.BackClicked) },
        onStartMainTest = { viewModel.onEvent(Event.StartMainTest) },
        onResponseKey = keyboardResponses(
            keyboard,
            mapOf(ResponseKeys.BRAKE.key to { pressNanos -> viewModel.onEvent(Event.OnBrakePressed(pressNanos)) })
        ),
        resultLabels = ResultLabels(
            average = "Средняя абсолютная ошибка",
            standardDeviation = "Стандартное отклонение ошибки",
            validCount = "Остановок учтено"
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RoadView(
                lampIndex = 0,
                approach = ApproachToStopLine(progress = progress),
                onBrakePress = if (keyboard) null else { pressNanos -> viewModel.onEvent(Event.OnBrakePressed(pressNanos)) },
                brakePressed = keyboard && ResponseKeys.BRAKE.key in LocalPressedResponseKeys.current
            )
            RoadSceneHeader(
                status = testStatusText(state.phase, state.countdown, state.results.presented, state.testDTO.count, state.isPractice),
                instruction = "${responseHint(keyboard, ResponseKeys.BRAKE).replaceFirstChar { it.uppercase() }} точно в момент, когда машина окажется на стоп-линии",
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
