package testmenu.store

import data.model.dto.test.InputDevice
import data.model.regular.user.Interval
import model.tests.ReactionTest

/** Шаги окна перед запуском теста. */
enum class StartStep { PARAMETERS, TUTORIAL }

data class State(
    val id: Long = -1L,
    val reactionTests: List<ReactionTest>,
    /** Тест, для которого открыто окно перед запуском; null — окно закрыто. */
    val selectedTest: ReactionTest? = null,
    val step: StartStep = StartStep.PARAMETERS,
    val count: Int = 0,
    val interval: Interval = Interval(),
    val inputDevice: InputDevice = InputDevice.KEYBOARD,
)
