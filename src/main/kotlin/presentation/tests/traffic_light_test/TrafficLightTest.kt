package presentation.tests.traffic_light_test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import presentation.other.components.BackButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.size.ConstantSize
import presentation.tests.traffic_light_test.components.TestFinishedComponent
import presentation.tests.traffic_light_test.components.TestProccessComponent
import presentation.tests.traffic_light_test.store.Action
import presentation.tests.traffic_light_test.store.Event
import presentation.tests.traffic_light_test.store.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficLightTest(
    onEvent: (Event) -> Unit,
    trafficLightTestState: MutableStateFlow<State>,
    trafficLightTestAction: MutableStateFlow<Action>,
    getAverage: () -> Double,
    getStdDeviation: () -> Double
) {

    val state = trafficLightTestState.collectAsState().value

    LaunchedEffect(trafficLightTestAction) {
        trafficLightTestAction.emit(Action.StartTimer)
    }

    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackCLicked) }
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
            if (state.userClicked == state.count)
                with(state) {
                    TestFinishedComponent(
                        getAverage = getAverage,
                        getStdDeviation = getStdDeviation,
                        count = count,
                        errors = errors,
                        id = user.id
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
                    count = count,
                    currentLampIndex = currentLampIndex
                )
            }
            if (state.userClicked != state.count) {
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
                            onEvent(Event.OnTrafficLightLampButtonClicked(clickedLampIndex = 0))
                        },
                        modifier = Modifier.size(ConstantSize.trafficLightLampButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {}
                    Button(
                        onClick = {
                            onEvent(Event.OnTrafficLightLampButtonClicked(clickedLampIndex = 1))
                        },
                        modifier = Modifier.size(ConstantSize.trafficLightLampButtonSize),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Yellow
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {}
                    Button(
                        onClick = {
                            onEvent(Event.OnTrafficLightLampButtonClicked(clickedLampIndex = 2))
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