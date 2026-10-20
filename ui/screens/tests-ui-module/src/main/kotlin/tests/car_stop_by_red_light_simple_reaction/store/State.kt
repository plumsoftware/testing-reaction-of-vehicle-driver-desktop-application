package tests.car_stop_by_red_light_simple_reaction.store

import data.model.dto.test.TestDTO
import other.components.SaveStatus
import tests.common.COUNTDOWN_SECONDS
import tests.common.TestPhase
import tests.common.TrialResults

data class State(
    val phase: TestPhase = TestPhase.COUNTDOWN,
    val countdown: Int = COUNTDOWN_SECONDS,
    /** Идентификатор текущего предъявления красного сигнала, null вне фазы STIMULUS. */
    val stimulusId: Int? = null,
    /** Последняя попытка завершилась пропуском: машина проехала на красный. */
    val lastTrialMissed: Boolean = false,
    val results: TrialResults = TrialResults(),

    val testDTO: TestDTO = TestDTO(),
    val saveStatus: SaveStatus = SaveStatus.SAVING,
)
