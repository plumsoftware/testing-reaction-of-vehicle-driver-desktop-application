package tests.traffic_light_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.BackButton
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
import other.components.TestFinishedComponent
import other.components.TestProccessComponent
import tests.traffic_light_test.store.Effect
import tests.traffic_light_test.store.Event
import tests.traffic_light_test.viewmodel.TrafficLightTestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficLightTest(
    workBookStorage: WorkbookStorage,
    settings: Settings,
    testDTO: TestDTO,
    sessionStorage: SessionStorage,
    navigator: Navigator
) {

    val trafficLightTestViewModel =
        viewModel(modelClass = TrafficLightTestViewModel::class) {
            TrafficLightTestViewModel(
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
                        .fillMaxWidth()
                        .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = ExtensionPadding.mediumHorizontalArrangementCenter
                ) {
                    Button(
                        onClick = {
                            trafficLightTestViewModel.onEvent(
                                Event.OnTrafficLightLampButtonClicked(
                                    clickedLampIndex = 0
                                )
                            )
                        },
                        modifier = Modifier.size(ConstantSize.trafficLightLampButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {}
                    Button(
                        onClick = {
                            trafficLightTestViewModel.onEvent(
                                Event.OnTrafficLightLampButtonClicked(
                                    clickedLampIndex = 1
                                )
                            )
                        },
                        modifier = Modifier.size(ConstantSize.trafficLightLampButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Yellow
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {}
                    Button(
                        onClick = {
                            trafficLightTestViewModel.onEvent(
                                Event.OnTrafficLightLampButtonClicked(
                                    clickedLampIndex = 2
                                )
                            )
                        },
                        modifier = Modifier.size(ConstantSize.trafficLightLampButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {}
                }
//            endregion
            }
        }
    }
}