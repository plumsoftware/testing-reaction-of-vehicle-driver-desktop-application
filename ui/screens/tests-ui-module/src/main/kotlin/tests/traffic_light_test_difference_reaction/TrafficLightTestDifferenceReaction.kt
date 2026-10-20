package tests.traffic_light_test_difference_reaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.test.GasPedalButton
import other.components.test.TRAFFIC_LIGHT_HEIGHT_IN_LAMPS
import other.components.test.TrafficLight
import tests.common.TestScreenScaffold
import tests.common.statusTextStyle
import tests.common.testStatusText
import tests.traffic_light_test_difference_reaction.store.Effect
import tests.traffic_light_test_difference_reaction.store.Event
import tests.traffic_light_test_difference_reaction.viewmodel.TrafficLightTestDifferenceReactionViewModel

/** Высота педали в размерах лампы. */
private const val PEDAL_HEIGHT_IN_LAMPS = 3.2f

@Composable
fun TrafficLightTestDifferenceReaction(
    workBookStorage: WorkbookStorage,
    settings: Settings,
    testDTO: TestDTO,
    sessionStorage: SessionStorage,
    navigator: Navigator
) {

    val viewModel =
        viewModel(modelClass = TrafficLightTestDifferenceReactionViewModel::class) {
            TrafficLightTestDifferenceReactionViewModel(
                workbookStorage = workBookStorage,
                settings = settings,
                testDTO = testDTO,
                sessionStorage = sessionStorage
            )
        }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackClicked -> {
                    navigator.goBack()
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(Event.StartTimer)
    }

    TestScreenScaffold(
        phase = state.phase,
        result = state.testDTO,
        saveStatus = state.saveStatus,
        onBack = { viewModel.onEvent(Event.BackCLicked) }
    ) {
        // Высота сцены: статус + светофор + педаль ≈ 10.1 размера лампы
        val lampSize = min(
            (maxHeight - 72.dp) / (TRAFFIC_LIGHT_HEIGHT_IN_LAMPS + PEDAL_HEIGHT_IN_LAMPS + 1f),
            maxWidth / 5f
        ).coerceIn(16.dp, 140.dp)

        Column(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(lampSize * 0.5f, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = testStatusText(state.phase, state.countdown, state.results.presented, state.testDTO.count),
                style = statusTextStyle()
            )

            val stimulusId = state.stimulusId
            TrafficLight(
                currentLampIndex = state.currentLampIndex,
                lampSize = lampSize,
                stimulusKey = stimulusId,
                onLampDrawn = { drawNanos ->
                    if (stimulusId != null) viewModel.onEvent(Event.OnStimulusDrawn(stimulusId, drawNanos))
                }
            )

            GasPedalButton(
                height = lampSize * PEDAL_HEIGHT_IN_LAMPS,
                onPress = { pressNanos -> viewModel.onEvent(Event.OnGasPedalPressed(pressNanos)) }
            )
        }
    }
}
