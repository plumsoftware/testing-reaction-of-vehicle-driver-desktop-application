package tests.car_stop_by_red_light_simple_reaction.viewmodel

import data.model.dto.database.SessionDTO
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import tests.car_stop_by_red_light_simple_reaction.store.Effect
import tests.car_stop_by_red_light_simple_reaction.store.Event
import tests.car_stop_by_red_light_simple_reaction.store.State
import java.util.Calendar
import kotlin.coroutines.CoroutineContext
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class CarStopByRedLightSimpleReactionViewModel(
    testDTO: TestDTO,
    private val settings: Settings,
    private val workBookStorage: WorkbookStorage,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    val state = MutableStateFlow(State(testDTO = testDTO))
    val effect = MutableSharedFlow<Effect>()

    private lateinit var signalJob: Job
    private val supervisorJob: CoroutineContext = viewModelScope.coroutineContext + SupervisorJob()

    init {
        println("Car stop by red light Test ViewModel created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.StartInitTimer -> {
                viewModelScope
                    .launch {
                        while (state.value.startCountDownTimer != 0) {
                            delay(1.seconds)
                            withContext(context = Dispatchers.Main) {
                                state.update {
                                    it.copy(startCountDownTimer = state.value.startCountDownTimer - 1)
                                }
                            }
                        }
                    }
                    .invokeOnCompletion {
                        animLoop()
                    }
            }

            Event.ClickOnStopPedal -> {
                if (state.value.isAnimPlaying) {
                    signalJob.cancel()

                    val end = Calendar.getInstance()
                    state.update {
                        it.copy(
                            isAnimPlaying = false,
                            end = end.timeInMillis
                        )
                    }
                    val interval: Long = state.value.end - state.value.start
                    state.value.intervals.add(interval)

                    println("Interval is $interval")

                    viewModelScope.launch(Dispatchers.Default) {
                        updateUserClickedState {
                            state.update {
                                it.copy(userClicked = state.value.userClicked + 1)
                            }
                        }
                    }
                    println("${state.value.userClicked}")
                    if (state.value.userClicked < state.value.testDTO.count) {
                        viewModelScope.launch(Dispatchers.Main) {
                            resetCarPosition()
                        }
                        animLoop()
                    } else {
                        state.update {
                            it.copy(testIsFinished = true)
                        }
                        getStdDeviation()
                        getAverage()
                        registerData()
                    }
                }
            }

            Event.BackClicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }
        }
    }

    private fun animLoop() {
        generateRandomInterval()
        signalJob = viewModelScope.launch(Dispatchers.Default) {
            generateSignal()
        }
    }

    private fun generateRandomInterval() {
        val intervalSignal =
            Random.nextLong(
                state.value.testDTO.interval.start,
                state.value.testDTO.interval.finish
            )

        state.update {
            it.copy(
                intervalSignal = intervalSignal,
            )
        }
    }

    private suspend fun generateSignal() {
        delay(state.value.intervalSignal)
        val start = Calendar.getInstance()
        state.update {
            it.copy(
                isAnimPlaying = true,
                start = start.timeInMillis,
            )
        }

        while (state.value.currentDistance < state.value.finalDistance) {
            withContext(Dispatchers.Main) {
                state.update {
                    it.copy(currentDistance = state.value.currentDistance + 28)
                }
            }
            delay(16.67.milliseconds)
        }

        state.value.intervals.add(0L)
        updateUserClickedState {
            state.update {
                it.copy(
                    isAnimPlaying = false,
                    errors = state.value.errors + 1,
                    userClicked = state.value.userClicked + 1
                )
            }
        }
        resetCarPosition()
        if (state.value.userClicked != state.value.testDTO.count) {
            animLoop()
        } else {
            withContext(Dispatchers.Main) {
                state.update {
                    it.copy(testIsFinished = true)
                }
            }
            getStdDeviation()
            getAverage()
            registerData()
        }
    }

    private fun updateUserClickedState(block: () -> Unit) {
        block.invoke()
    }

    private suspend fun resetCarPosition() {
        delay(1.seconds)
        state.update {
            it.copy(currentDistance = 0f)
        }
    }

    private fun getAverage() {
        val average = state.value.intervals.average()
        state.update {
            it.copy(testDTO = state.value.testDTO.copy(averageValue = average / 1000))
        }
    }

    private fun getStdDeviation() {
        val average = state.value.intervals.average()
        val variance = state.value.intervals.map { (it - average) * (it - average) }.average()
        state.update {
            it.copy(testDTO = state.value.testDTO.copy(stdDeviationValue = sqrt(variance)))
        }
    }

    private fun registerData() {
        state.update {
            it.copy(
                testDTO = state.value.testDTO.copy(
                    errorsCount = state.value.errors,
                    intervals = state.value.intervals,
                )
            )
        }
        val calendarForGetDate = Calendar.getInstance()

        val jobSession = viewModelScope.launch(supervisorJob) {
            val sessionDTO = SessionDTO(
                sessionId = 0,
                userId = state.value.testDTO.user.id,
                testId = state.value.testDTO.testMode?.id!!,
                testYear = calendarForGetDate.get(Calendar.YEAR),
                testMonth = calendarForGetDate.get(Calendar.MONTH),
                testDay = calendarForGetDate.get(Calendar.DAY_OF_MONTH),
                testHourOfDay24h = calendarForGetDate.get(Calendar.HOUR_OF_DAY),
                testMinuteOfHour = calendarForGetDate.get(Calendar.MINUTE),
                averageValue = state.value.testDTO.averageValue,
                standardDeviation = state.value.testDTO.stdDeviationValue,
                count = state.value.testDTO.count,
                errors = state.value.testDTO.errorsCount!!,
                experience = state.value.testDTO.user.experience.toString(),
                userAge = state.value.testDTO.user.age.toString(),
                drivingLicenseCategory = state.value.testDTO.user.drivingLicenseCategory,
                signalInterval = state.value.testDTO.interval
            )
            sessionStorage.insertOrAbort(sessionDTO = sessionDTO)
            val sessionId = sessionStorage.getLastSessionIdUseCase()
            state.update {
                it.copy(
                    testDTO = state.value.testDTO.copy(
                        sessionId = sessionId
                    )
                )
            }
        }
        viewModelScope.launch(supervisorJob) {
            jobSession.join()
            workBookStorage.createWorkbookIfNotExists(
                folderPath = settings.localFolderToTable,
                dataFormats = settings.dataFormats
            )
            workBookStorage.writeDataToWorkbook(
                testDTO = state.value.testDTO,
                dataFormats = settings.dataFormats,
                folderPath = settings.localFolderToTable
            )
        }
    }
}