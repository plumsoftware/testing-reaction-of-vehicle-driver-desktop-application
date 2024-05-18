package presentation.tests.traffic_light_test

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import presentation.other.components.BackButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.size.ConstantSize
import presentation.tests.traffic_light_test.store.Action
import presentation.tests.traffic_light_test.store.Event
import presentation.tests.traffic_light_test.store.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrafficLightTest(
    onEvent: (Event) -> Unit,
    trafficLightTestState: MutableStateFlow<State>,
    trafficLightTestAction: MutableStateFlow<Action>
) {

    val state = trafficLightTestState.collectAsState().value

    LaunchedEffect(null) {
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
                Text(
                    text = "Тестирование окончено",
                    style = MaterialTheme.typography.headlineMedium
                )
            else
                Text(
                    text = if (state.startTimerTime != 0) "Начало через ${state.startTimerTime}" else "Тест начался",
                    style = MaterialTheme.typography.headlineMedium
                )
            Box {
                if (state.userClicked != state.count) {
                    Image(painter = painterResource("traffic_light.png"), contentDescription = "Изображение светофора")
//                region::Lamps
                    Box(
                        modifier = Modifier
                            .padding(top = 53.dp)
                            .clip(shape = RoundedCornerShape(200.dp))
                            .align(Alignment.TopCenter)
                            .background(if (state.currentLampIndex == 0) Color.Red else Color.Gray)
                            .size(ConstantSize.trafficLightLampSize)
                    )

                    Box(
                        modifier = Modifier
                            .padding(bottom = 60.dp)
                            .clip(shape = RoundedCornerShape(200.dp))
                            .align(Alignment.Center)
                            .background(if (state.currentLampIndex == 1) Color.Yellow else Color.Gray)
                            .size(ConstantSize.trafficLightLampSize)
                    )

                    Box(
                        modifier = Modifier
                            .padding(bottom = 110.dp)
                            .clip(shape = RoundedCornerShape(200.dp))
                            .align(Alignment.BottomCenter)
                            .background(if (state.currentLampIndex == 2) Color.Green else Color.Gray)
                            .size(ConstantSize.trafficLightLampSize)
                    )
//                endregion
                }
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