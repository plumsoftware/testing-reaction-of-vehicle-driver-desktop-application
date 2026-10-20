package tests.pvt_brake_lights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.test.LeadCar
import other.components.test.RoadView
import tests.common.RoadSceneHeader
import tests.common.TestPhase
import tests.common.TestScreenScaffold
import tests.common.rememberRoadScroll
import tests.pvt_brake_lights.store.Effect
import tests.pvt_brake_lights.store.Event
import tests.pvt_brake_lights.viewmodel.PvtBrakeLightsViewModel

/**
 * PVT в дорожном варианте. Номер попытки и оставшееся время не показываются:
 * в стандартном PVT испытуемый не знает, сколько осталось.
 */
@Composable
fun PvtBrakeLights(
    durationMinutes: Int,
    settings: Settings,
    workBookStorage: WorkbookStorage,
    testDTO: TestDTO,
    sessionStorage: SessionStorage,
    navigator: Navigator
) {
    val viewModel: PvtBrakeLightsViewModel =
        viewModel(modelClass = PvtBrakeLightsViewModel::class) {
            PvtBrakeLightsViewModel(
                testDTO = testDTO,
                durationMinutes = durationMinutes,
                settings = settings,
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
    val roadScroll = rememberRoadScroll(isDriving = state.phase != TestPhase.COUNTDOWN)
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
                leadCar = LeadCar(brakeLightsOn = state.phase == TestPhase.STIMULUS),
                onBrakePress = { pressNanos -> viewModel.onEvent(Event.OnBrakePressed(pressNanos)) },
                stimulusKey = if (state.phase == TestPhase.STIMULUS) stimulusId else null,
                onStimulusDrawn = { drawNanos ->
                    if (stimulusId != null) viewModel.onEvent(Event.OnStimulusDrawn(stimulusId, drawNanos))
                }
            )
            RoadSceneHeader(
                status = if (state.phase == TestPhase.COUNTDOWN) "Начало через ${state.countdown}" else "Тест $durationMinutes мин",
                instruction = "Загорелись стоп-сигналы впереди идущей машины — нажмите тормоз",
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // «Приборная панель»: время реакции после ответа, как в стандартном PVT
            val feedback = state.feedbackText
            if (feedback != null) {
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFFFFC400),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B1F24))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}
