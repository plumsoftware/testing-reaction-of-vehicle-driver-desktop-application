package tests.traffic_light_test_choice_reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import data.model.regular.settings.workbookFiles
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import model.tests.ResponseKeys
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.test.TRAFFIC_LIGHT_HEIGHT_IN_LAMPS
import other.components.test.TrafficLight
import other.components.test.onPressTimestamp
import other.components.test.trafficLightLampColors
import tests.common.LocalPressedResponseKeys
import tests.common.TestScreenScaffold
import tests.common.keyboardResponses
import tests.common.respondsWithKeyboard
import tests.common.statusTextStyle
import tests.common.testStatusText
import tests.traffic_light_test_choice_reaction.store.Effect
import tests.traffic_light_test_choice_reaction.store.Event
import tests.traffic_light_test_choice_reaction.viewmodel.TrafficLightTestChoiceReactionViewModel

@Composable
fun TrafficLightTestChoiceReaction(
    workBookStorage: WorkbookStorage,
    settings: Settings,
    testDTO: TestDTO,
    sessionStorage: SessionStorage,
    navigator: Navigator
) {

    val viewModel =
        viewModel(modelClass = TrafficLightTestChoiceReactionViewModel::class) {
            TrafficLightTestChoiceReactionViewModel(
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

    val keyboard = state.testDTO.respondsWithKeyboard

    TestScreenScaffold(
        phase = state.phase,
        result = state.testDTO,
        saveStatus = state.saveStatus,
        resultFiles = settings.workbookFiles,
        onBack = { viewModel.onEvent(Event.BackCLicked) },
        onStartMainTest = { viewModel.onEvent(Event.StartMainTest) },
        onResponseKey = keyboardResponses(
            keyboard,
            ResponseKeys.LAMPS.withIndex().associate { (index, binding) ->
                binding.key to { pressNanos: Long ->
                    viewModel.onEvent(Event.OnLampButtonPressed(lampIndex = index, pressNanos = pressNanos))
                }
            }
        )
    ) {
        // Высота сцены: статус + светофор + кнопки ≈ 8.2 размера лампы; ширина кнопок ≈ 7.8 лампы
        val lampSize = min((maxHeight - 72.dp) / (TRAFFIC_LIGHT_HEIGHT_IN_LAMPS + 2.3f), maxWidth / 7.8f)
            .coerceIn(16.dp, 140.dp)

        Column(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(lampSize * 0.5f, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = testStatusText(state.phase, state.countdown, state.results.presented, state.testDTO.count, state.isPractice),
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

            Row(horizontalArrangement = Arrangement.spacedBy(lampSize * 0.4f)) {
                trafficLightLampColors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(width = lampSize * 2.2f, height = lampSize)
                            .clip(MaterialTheme.shapes.small)
                            .background(color)
                            .then(
                                if (keyboard) Modifier
                                else Modifier.onPressTimestamp { pressNanos ->
                                    viewModel.onEvent(Event.OnLampButtonPressed(lampIndex = index, pressNanos = pressNanos))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // При ответе с клавиатуры на кнопке подписана её клавиша
                        if (keyboard) {
                            Text(
                                text = ResponseKeys.LAMPS[index].keyLabel,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
