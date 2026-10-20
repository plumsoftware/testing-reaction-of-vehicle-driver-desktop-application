package tests.car_stop_by_red_light_simple_reaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import tests.common.RoadSceneHeader
import tests.common.TestScreenScaffold
import tests.common.rememberRoadScroll
import tests.common.testStatusText

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

    val roadScroll = rememberRoadScroll(isDriving)

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
            RoadSceneHeader(
                status = testStatusText(state.phase, state.countdown, state.results.presented, state.testDTO.count),
                instruction = "Загорелся красный — нажмите тормоз",
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
