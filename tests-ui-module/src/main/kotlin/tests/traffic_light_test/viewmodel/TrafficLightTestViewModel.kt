package tests.traffic_light_test.viewmodel

import data.model.dto.test.TestDTO
import data.model.dto.database.SessionDTO
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import tests.traffic_light_test.store.Effect
import tests.traffic_light_test.store.Event
import tests.traffic_light_test.store.State
import java.util.Calendar
import kotlin.math.sqrt
import kotlin.random.Random

class TrafficLightTestViewModel(
    private val workbookStorage: WorkbookStorage,
    settings: Settings,
    testDTO: TestDTO,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    val state = MutableStateFlow(
        State(
            dataFormats = settings.dataFormats,
            localFolderToTable = settings.localFolderToTable,
            testDTO = testDTO
        )
    )
    val effect = MutableSharedFlow<Effect>()

    init {
        println("Traffic light Test ViewModel created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }

            is Event.OnTrafficLightLampButtonClicked -> {

                registerUserReactionTime()
                registerErrors(event.clickedLampIndex)

                if (state.value.userClicked != state.value.testDTO.count)
                    generateRandomInterval()
                else {
                    stopTest()
                    viewModelScope.launch {
                        preRegisterDataDatabaseCompile()
                        registerDataInDatabase()
                    }
                    println("Test is finished")
                }
            }

            Event.StartTimer -> {
                viewModelScope.launch {
                    clearStartData()
                    while (state.value.startTimerTime > 0) {
                        delay(1000)
                        val temp = state.value.startTimerTime - 1
                        state.update {
                            it.copy(
                                startTimerTime = temp
                            )
                        }
                    }
                    if (state.value.startTimerTime == 0) {
                        generateRandomSignal()
                    }
                    println("Count is ${state.value.testDTO.count}")
                }
            }
        }
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

                val intervalSignal =
                    Random.nextLong(
                        state.value.testDTO.interval.start,
                        state.value.testDTO.interval.finish
                    )

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

    private suspend fun preRegisterDataDatabaseCompile() {
        workbookStorage.createWorkbookIfNotExists(
            folderPath = state.value.localFolderToTable,
            dataFormats = state.value.dataFormats
        )
    }

    private suspend fun registerDataInDatabase() {
        val calendarForGetDate = Calendar.getInstance()
        val testDTO = state.value.testDTO.copy(
            errorsCount = state.value.errors,
            sessionId = sessionStorage.getLastSessionIdUseCase(),
            averageValue = getAverage(),
            stdDeviationValue = getStdDeviation()
        )
        val sessionDTO = SessionDTO(
            sessionId = 0,
            userId = testDTO.user.id,
            testId = testDTO.testMode?.id!!,
            testYear = calendarForGetDate.get(Calendar.YEAR),
            testMonth = calendarForGetDate.get(Calendar.MONTH),
            testDay = calendarForGetDate.get(Calendar.DAY_OF_MONTH),
            testHourOfDay24h = calendarForGetDate.get(Calendar.HOUR_OF_DAY),
            testMinuteOfHour = calendarForGetDate.get(Calendar.MINUTE),
            averageValue = getAverage(),
            standardDeviation = getStdDeviation(),
            count = testDTO.count,
            errors = testDTO.errorsCount!!,
            experience = testDTO.user.experience.toString(),
            userAge = testDTO.user.age.toString(),
            drivingLicenseCategory = testDTO.user.drivingLicenseCategory,
            signalInterval = testDTO.interval
        )
        workbookStorage.writeDataToWorkbook(
            testDTO = testDTO,
            folderPath = state.value.localFolderToTable,
            dataFormats = state.value.dataFormats
        )
        sessionStorage.insertOrAbort(
            sessionDTO = sessionDTO
        )
    }

    private fun stopTest() {
        state.update {
            it.copy(
                currentLampIndex = -1
            )
        }
    }

    internal fun getAverage(): Double {
        val average = state.value.intervals.average()
        return average / 1000
    }

    internal fun getStdDeviation(): Double {
        val average = state.value.intervals.average()
        val variance = state.value.intervals.map { (it - average) * (it - average) }.average()
        return sqrt(variance)
    }

    private fun clearStartData() {
        state.update {
            it.copy(
                currentLampIndex = -1,
                intervals = mutableListOf(),
                startTimerTime = 10,
                userClicked = 0,
                errors = 0
            )
        }
    }
}