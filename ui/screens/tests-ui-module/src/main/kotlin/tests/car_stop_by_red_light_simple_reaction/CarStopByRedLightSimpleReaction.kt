package tests.car_stop_by_red_light_simple_reaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.test.RoadView
import tests.car_stop_by_red_light_simple_reaction.store.Effect
import tests.car_stop_by_red_light_simple_reaction.store.Event
import tests.car_stop_by_red_light_simple_reaction.viewmodel.CarStopByRedLightSimpleReactionViewModel
import tests.common.TestPhase
import tests.common.TestScreenScaffold
import tests.common.statusTextStyle
import tests.common.testStatusText

/** Скорость движения разметки, периодов разметки в секунду (только визуально). */
private const val ROAD_SCROLL_PERIODS_PER_SECOND = 4f

@Composable
fun CarStopByRedLightSimpleReaction(
    settings: Settings,
    workBookStorage: WorkbookStorage,
    testDTO: TestDTO,
    sessionStorage: SessionStorage,
    navigator: Navigator
) {
    val viewModel: CarStopByRedLightSimpleReactionViewModel =
        viewModel(modelClass = CarStopByRedLightSimpleReactionViewModel::class) {
            CarStopByRedLightSimpleReactionViewModel(
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
                Effect.BackClicked -> {
                    navigator.goBack()
                }
            }
        }
    }

    val state by viewModel.state.collectAsState()

    // Машина едет до торможения; при проезде на красный продолжает ехать
    val isDriving = state.phase == TestPhase.FOREPERIOD || state.phase == TestPhase.STIMULUS ||
            (state.phase == TestPhase.FEEDBACK && state.lastTrialMissed)

    var roadScroll by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isDriving) {
        if (!isDriving) return@LaunchedEffect
        // Смещение разметки считается от времени кадра: скорость одинакова при любой частоте кадров
        var lastFrameNanos = withFrameNanos { it }
        while (true) {
            withFrameNanos { frameNanos ->
                val seconds = (frameNanos - lastFrameNanos) / 1_000_000_000f
                roadScroll = (roadScroll + seconds * ROAD_SCROLL_PERIODS_PER_SECOND) % 1f
                lastFrameNanos = frameNanos
            }
        }
    }

    // Красный горит с момента сигнала до конца паузы после попытки
    val lampIndex = if (state.phase == TestPhase.STIMULUS || state.phase == TestPhase.FEEDBACK) 0 else 2
    val stimulusId = state.stimulusId

    TestScreenScaffold(
        phase = state.phase,
        result = state.testDTO,
        saveStatus = state.saveStatus,
        onBack = { viewModel.onEvent(Event.BackClicked) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RoadView(
                roadScroll = roadScroll,
                lampIndex = lampIndex,
                onBrakePress = { pressNanos -> viewModel.onEvent(Event.OnBrakePressed(pressNanos)) },
                stimulusKey = if (state.phase == TestPhase.STIMULUS) stimulusId else null,
                onStimulusDrawn = { drawNanos ->
                    if (stimulusId != null) viewModel.onEvent(Event.OnStimulusDrawn(stimulusId, drawNanos))
                }
            )
            Text(
                text = testStatusText(state.phase, state.countdown, state.results.presented, state.testDTO.count),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.TopCenter),
                style = statusTextStyle().copy(color = Color.Black)
            )
        }
    }
}
