package tests.traffic_light_test_difference_reaction.store

import data.model.dto.test.TestDTO
import other.components.SaveStatus
import tests.common.COUNTDOWN_SECONDS
import tests.common.TestPhase
import tests.common.TrialResults

data class State(
    val phase: TestPhase = TestPhase.COUNTDOWN,
    val countdown: Int = COUNTDOWN_SECONDS,
    /** Горящая лампа (0 — красный, 1 — жёлтый, 2 — зелёный) или -1. */
    val currentLampIndex: Int = -1,
    /** Идентификатор текущего предъявления сигнала, null вне фазы STIMULUS. */
    val stimulusId: Int? = null,
    val results: TrialResults = TrialResults(),
    /** Идёт тренировка: попытки не сохраняются. */
    val isPractice: Boolean = true,

    val testDTO: TestDTO = TestDTO(),
    val saveStatus: SaveStatus = SaveStatus.SAVING,
)
