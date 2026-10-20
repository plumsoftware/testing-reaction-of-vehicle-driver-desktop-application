package tests.horn_brake_reaction.store

import data.model.dto.test.TestDTO
import other.components.SaveStatus
import tests.common.COUNTDOWN_SECONDS
import tests.common.TestPhase
import tests.common.TrialResults

data class State(
    val phase: TestPhase = TestPhase.COUNTDOWN,
    val countdown: Int = COUNTDOWN_SECONDS,
    /** Последняя попытка завершилась пропуском сигнала. */
    val lastTrialMissed: Boolean = false,
    val results: TrialResults = TrialResults(),
    /** Ошибка звукового устройства; тест в этом случае не проводится. */
    val audioError: String? = null,

    val testDTO: TestDTO = TestDTO(),
    val saveStatus: SaveStatus = SaveStatus.SAVING,
)
