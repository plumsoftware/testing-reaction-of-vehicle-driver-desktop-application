package testmenu.store

import data.model.dto.test.InputDevice
import data.model.dto.test.TestDTO
import data.model.regular.user.Interval
import model.tests.ReactionTest

sealed class Event {
    /** Открыть окно перед запуском; [testDTO] — текущие данные, из них берутся значения по умолчанию. */
    data class TestClicked(val test: ReactionTest, val testDTO: TestDTO) : Event()

    data class CountChanged(val count: Int) : Event()

    data class IntervalChanged(val interval: Interval) : Event()

    data class InputDeviceChanged(val inputDevice: InputDevice) : Event()

    /** Параметры выбраны — к обучению. */
    data object ParametersConfirmed : Event()

    /** С обучения обратно к параметрам. */
    data object BackToParameters : Event()

    data object DialogDismissed : Event()

    data class StartClicked(val testDTO: TestDTO, val withPractice: Boolean) : Event()

    data object BackCLicked : Event()
}
