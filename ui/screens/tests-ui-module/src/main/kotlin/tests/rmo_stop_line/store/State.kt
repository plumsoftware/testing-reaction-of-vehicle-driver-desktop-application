package tests.rmo_stop_line.store

import data.model.dto.test.TestDTO
import other.components.SaveStatus
import tests.common.COUNTDOWN_SECONDS
import tests.common.TestPhase
import tests.common.TrialResults

data class State(
    val phase: TestPhase = TestPhase.COUNTDOWN,
    val countdown: Int = COUNTDOWN_SECONDS,
    /** System.nanoTime() начала движения текущей попытки; null — машина стоит. */
    val motionStartNanos: Long? = null,
    /** Время движения от старта до стоп-линии в текущей попытке, мс. */
    val travelTimeMs: Long = 0L,
    /** Положение, в котором машина остановлена торможением (доля пути до линии). */
    val stoppedProgress: Float? = null,
    val results: TrialResults = TrialResults(),

    val testDTO: TestDTO = TestDTO(),
    val saveStatus: SaveStatus = SaveStatus.SAVING,
)
