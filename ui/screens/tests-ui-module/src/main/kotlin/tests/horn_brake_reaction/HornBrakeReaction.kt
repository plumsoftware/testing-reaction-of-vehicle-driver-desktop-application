package tests.horn_brake_reaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.test.RoadView
import tests.common.RoadSceneHeader
import tests.common.TestPhase
import tests.common.TestScreenScaffold
import tests.common.rememberRoadScroll
import tests.common.testStatusText
import tests.horn_brake_reaction.store.Effect
import tests.horn_brake_reaction.store.Event
import tests.horn_brake_reaction.viewmodel.HornBrakeReactionViewModel

@Composable
fun HornBrakeReaction(
    settings: Settings,
    workBookStorage: WorkbookStorage,
    testDTO: TestDTO,
    sessionStorage: SessionStorage,
    navigator: Navigator
) {
    val viewModel: HornBrakeReactionViewModel =
        viewModel(modelClass = HornBrakeReactionViewModel::class) {
            HornBrakeReactionViewModel(
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
                Effect.BackClicked -> navigator.goBack()
            }
        }
    }

    val state by viewModel.state.collectAsState()

    // Машина едет до торможения; при пропуске сигнала продолжает ехать
    val isDriving = state.phase == TestPhase.FOREPERIOD || state.phase == TestPhase.STIMULUS ||
            (state.phase == TestPhase.FEEDBACK && state.lastTrialMissed)
    val roadScroll = rememberRoadScroll(isDriving)

    TestScreenScaffold(
        phase = state.phase,
        result = state.testDTO,
        saveStatus = state.saveStatus,
        onBack = { viewModel.onEvent(Event.BackClicked) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            RoadView(
                roadScroll = roadScroll,
                onBrakePress = { pressNanos -> viewModel.onEvent(Event.OnBrakePressed(pressNanos)) }
            )
            val audioError = state.audioError
            if (audioError != null) {
                Text(
                    text = audioError,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            } else {
                RoadSceneHeader(
                    status = testStatusText(state.phase, state.countdown, state.results.presented, state.testDTO.count),
                    instruction = "Услышали звуковой сигнал — нажмите тормоз",
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
