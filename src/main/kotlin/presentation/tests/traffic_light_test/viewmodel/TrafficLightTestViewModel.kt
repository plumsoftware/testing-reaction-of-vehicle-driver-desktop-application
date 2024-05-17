package presentation.tests.traffic_light_test.viewmodel

import data.Constants
import domain.storage.WorkbookStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.tests.traffic_light_test.store.Action
import presentation.tests.traffic_light_test.store.Event
import presentation.tests.traffic_light_test.store.Output
import presentation.tests.traffic_light_test.store.State
import java.util.Calendar
import kotlin.random.Random

class TrafficLightTestViewModel(
    private val output: (Output) -> Unit,
    private val workbookStorage: WorkbookStorage,
    private val dataFormats: Map<String, Boolean>,
    private val localFolderToTable: String
) : ViewModel() {

    val state = MutableStateFlow(State())
    val actions = MutableStateFlow<Action>(Action.StartTimer)

    init {
        println("Traffic light Test ViewModel created")
    }

    fun collectActions() {
        viewModelScope.launch {
            actions.collect { action ->
                when (action) {
                    Action.StartTimer -> {
                        while (state.value.startTimerTime > 0) {
                            delay(1000)
                            state.update {
                                it.copy(
                                    startTimerTime = state.value.startTimerTime - 1
                                )
                            }
                        }
                        if (state.value.startTimerTime == 0) {
                            actions.emit(Action.GenerateRandomSignal)
                        }
                        println("Count is ${state.value.count}")
                    }

                    Action.GenerateRandomSignal -> {
                        generateRandomSignal()
                    }
                }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                onOutput(Output.BackButtonClicked)
            }

            is Event.OnTrafficLightLampButtonClicked -> {

                registerUserReactionTime()
                registerErrors(event.clickedLampIndex)

                if (state.value.userClicked != state.value.count)
                    generateRandomInterval()
                else {
                    stopTest()
                    viewModelScope.launch {
                        registerDataInDatabase()
                    }
                    println("Test is finished")
                }
            }

            is Event.InitStartData -> state.update {
                it.copy(
                    user = event.user,
                    count = event.count
                )
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }

    private fun generateRandomSignal() {
        if (state.value.startTimerTime == 0) {
            val currentIndex = Random.nextInt(0, 3)

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()

            state.update {
                it.copy(
                    currentLampIndex = currentIndex,
                    start = calendar.timeInMillis
                )
            }
            println("==================")
            println(
                "Signal was shown at ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${
                    calendar.get(
                        Calendar.SECOND
                    )
                }"
            )
        }
    }

    private fun generateRandomInterval() {
        viewModelScope.launch {
            if (state.value.startTimerTime == 0 && state.value.currentLampIndex != -1) {
                state.update {
                    it.copy(
                        currentLampIndex = -1
                    )
                }

                val intervalSignal = Random.nextLong(2000, 10000)

                println("==================")
                println("Signal interval is ${intervalSignal}msc")

                delay(intervalSignal)
                generateRandomSignal()
            }
        }
    }

    private fun registerErrors(clickedLampIndex: Int) {
        if (state.value.currentLampIndex != clickedLampIndex) {
            state.update {
                it.copy(errors = state.value.errors + 1)
            }
        }
    }

    private fun registerUserReactionTime() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        val userInterval: Long = calendar.timeInMillis - state.value.start
        val userCalendar = Calendar.getInstance()
        userCalendar.timeInMillis = userInterval
        println("User reaction time is ${userCalendar.get(Calendar.SECOND)} seconds")

        state.value.intervals.add(userInterval)
        state.update {
            it.copy(
                end = calendar.timeInMillis,
                userClicked = state.value.userClicked + 1,
            )
        }

        println("==================")
        println(
            "Button was clicked at ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${
                calendar.get(
                    Calendar.SECOND
                )
            }"
        )
    }

    private suspend fun registerDataInDatabase() {
        workbookStorage.createWorkbookIfNotExistsUseCase(
            folderPath = localFolderToTable,
            dataFormats = dataFormats
        )
    }

    private fun stopTest() {
        state.update {
            it.copy(
                currentLampIndex = -1
            )
        }
    }
}