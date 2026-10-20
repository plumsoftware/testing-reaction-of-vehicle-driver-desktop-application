package tests.pvt_brake_lights.store

import data.model.dto.test.TestDTO
import other.components.SaveStatus
import tests.common.COUNTDOWN_SECONDS
import tests.common.TestPhase
import tests.common.TrialResults

data class State(
    val phase: TestPhase = TestPhase.COUNTDOWN,
    val countdown: Int = COUNTDOWN_SECONDS,
    /** Идентификатор текущего предъявления (стоп-сигналы горят), null вне фазы STIMULUS. */
    val stimulusId: Int? = null,
    /** Сообщение на «приборной панели» после ответа (время реакции или «Фальстарт»), 1 с. */
    val feedbackText: String? = null,
    val results: TrialResults = TrialResults(),

    val testDTO: TestDTO = TestDTO(),
    val saveStatus: SaveStatus = SaveStatus.SAVING,
)
