package testmenu.viewmodel

import data.constant.TestConstants
import data.model.dto.test.InputDevice
import data.model.regular.tests.TestMode
import data.model.regular.user.Interval
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.tests.allReactionTests
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import testmenu.store.Effect
import testmenu.store.Event
import testmenu.store.StartStep
import testmenu.store.State

class TestMenuViewModel : ViewModel() {

    val state = MutableStateFlow(
        State(
            reactionTests = allReactionTests
        )
    )
    val effect = MutableSharedFlow<Effect>()

    init {
        println("Test menu view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.TestClicked -> {
                state.update {
                    it.copy(
                        selectedTest = event.test,
                        step = StartStep.PARAMETERS,
                        inputDevice = event.testDTO.preTestState.inputDevice
                            ?.takeIf { device -> device in selectableInputDevices }
                            ?: it.inputDevice,
                        count = it.count.takeIf { count -> count > 0 }
                            ?: event.testDTO.count.takeIf { count -> count > 0 }
                            ?: DEFAULT_COUNT,
                        interval = it.interval.takeIf { interval -> interval != Interval() }
                            ?: event.testDTO.interval.takeIf { interval -> interval != Interval() }
                            ?: DEFAULT_INTERVAL
                    )
                }
            }

            is Event.CountChanged -> state.update { it.copy(count = event.count) }

            is Event.IntervalChanged -> state.update { it.copy(interval = event.interval) }

            is Event.InputDeviceChanged -> state.update { it.copy(inputDevice = event.inputDevice) }

            Event.ParametersConfirmed -> state.update { it.copy(step = StartStep.TUTORIAL) }

            Event.BackToParameters -> state.update { it.copy(step = StartStep.PARAMETERS) }

            Event.DialogDismissed -> state.update { it.copy(selectedTest = null) }

            is Event.StartClicked -> {
                val test = state.value.selectedTest ?: return
                val testDTO = event.testDTO.copy(
                    testMode = TestMode(name = test.name, id = test.id.toLong()),
                    count = state.value.count,
                    interval = state.value.interval,
                    preTestState = event.testDTO.preTestState.copy(inputDevice = state.value.inputDevice),
                    withPractice = event.withPractice
                )
                state.update { it.copy(selectedTest = null) }
                viewModelScope.launch {
                    effect.emit(Effect.TestClicked(route = test.route, testDTO = testDTO))
                }
            }

            Event.BackCLicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }
        }
    }

    companion object {
        /** Устройства, из которых выбирают в окне перед запуском теста. */
        val selectableInputDevices = listOf(InputDevice.KEYBOARD, InputDevice.MOUSE)

        private const val DEFAULT_COUNT = 10
        private val DEFAULT_INTERVAL = TestConstants.Tests.intervals[1]
    }
}
