package tests.car_stop_by_red_light_simple_reaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import other.components.BackButton
import other.components.TestFinishedComponent
import other.components.test.RoadView
import tests.car_stop_by_red_light_simple_reaction.store.Effect
import tests.car_stop_by_red_light_simple_reaction.store.Event
import tests.car_stop_by_red_light_simple_reaction.store.State
import tests.car_stop_by_red_light_simple_reaction.viewmodel.CarStopByRedLightSimpleReactionViewModel

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

    val state = viewModel.state.collectAsState()
    CarStopByRedLightSimpleReactionContent(state = state, onEvent = viewModel::onEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarStopByRedLightSimpleReactionContent(
    state: androidx.compose.runtime.State<State>,
    onEvent: (Event) -> Unit
) {
    Scaffold(
        topBar = {
            BackButton {
                onEvent(Event.BackClicked)
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.value.testIsFinished) {
                TestFinishedComponent(
                    modifier = Modifier.align(Alignment.Center),
                    getAverage = { state.value.testDTO.averageValue },
                    getStdDeviation = { state.value.testDTO.stdDeviationValue },
                    count = state.value.testDTO.count,
                    errors = state.value.errors,
                    id = state.value.testDTO.user.id
                )
            } else {
                RoadView(
                    onClick = { onEvent(Event.ClickOnStopPedal) },
                    offsetX = state.value.currentDistance
                )
                Text(
                    text = if (state.value.startCountDownTimer > 0) "Начало через ${state.value.startCountDownTimer}" else "Тест начался",
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.TopCenter),
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color.Black)
                )
            }
        }
    }
}
