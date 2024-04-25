package presentation.tests.traffic_light_test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import presentation.other.components.BackButton
import presentation.other.extension.padding.ExtensionPadding
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
            Text(text = "Начало через ${state.startTimerTime}", style = MaterialTheme.typography.headlineMedium)
        }
    }
}