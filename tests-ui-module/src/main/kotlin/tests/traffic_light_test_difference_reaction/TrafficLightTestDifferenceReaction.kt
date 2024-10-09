package tests.traffic_light_test_difference_reaction

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.BackButton
import other.extension.padding.ExtensionPadding
import other.components.TestFinishedComponent
import other.components.TestProccessComponent
import other.components.test.GasPedalButton
import tests.traffic_light_test_difference_reaction.store.Effect
import tests.traffic_light_test_difference_reaction.store.Event
import tests.traffic_light_test_difference_reaction.viewmodel.TrafficLightTestDifferenceReactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficLightTestDifferenceReaction(
    workBookStorage: WorkbookStorage,
    settings: Settings,
    testDTO: TestDTO,
    sessionStorage: SessionStorage,
    navigator: Navigator
) {

    val trafficLightTestViewModel =
        viewModel(modelClass = TrafficLightTestDifferenceReactionViewModel::class) {
            TrafficLightTestDifferenceReactionViewModel(
                workbookStorage = workBookStorage,
                settings = settings,
                testDTO = testDTO,
                sessionStorage = sessionStorage
            )
        }

    val state = trafficLightTestViewModel.state.collectAsState().value

    LaunchedEffect(key1 = Unit) {
        trafficLightTestViewModel.effect.collect { effect->
            when (effect) {
                Effect.BackClicked -> {
                    navigator.goBack()
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        trafficLightTestViewModel.onEvent(Event.StartTimer)
    }

    Scaffold(
        topBar = {
            BackButton(
                onClick = { trafficLightTestViewModel.onEvent(Event.BackCLicked) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.userClicked == state.testDTO.count)
                with(state) {
                    TestFinishedComponent(
                        getAverage = trafficLightTestViewModel::getAverage,
                        getStdDeviation = trafficLightTestViewModel::getStdDeviation,
                        count = testDTO.count,
                        errors = errors,
                        id = testDTO.user.id
                    )
                }
            else
                Text(
                    text = if (state.startTimerTime > 0) "Начало через ${state.startTimerTime}" else "Тест начался",
                    style = MaterialTheme.typography.headlineMedium
                )
            with(state) {
                TestProccessComponent(
                    userClicked = userClicked,
                    count = testDTO.count,
                    currentLampIndex = currentLampIndex
                )
            }
            if (state.userClicked != state.testDTO.count) {
//            region::Buttons
                Row(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxWidth()
                        .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = ExtensionPadding.mediumHorizontalArrangementCenter
                ) {

                    GasPedalButton {
                        trafficLightTestViewModel.onEvent(Event.OnTrafficLightLampButtonClicked)
                    }
                }
//            endregion
            }
        }
    }
}
