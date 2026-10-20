package tests.common

import data.model.dto.database.SessionDTO
import data.model.dto.test.ReactionStatistics
import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import data.model.regular.user.Interval
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.random.Random

/** Длительность обратного отсчёта перед первым сигналом, с. */
const val COUNTDOWN_SECONDS = 10

/**
 * Фазы теста:
 * COUNTDOWN — обратный отсчёт; FOREPERIOD — случайный интервал до сигнала;
 * STIMULUS — сигнал предъявлен, ждём реакцию; FEEDBACK — пауза после попытки;
 * FINISHED — итоги.
 */
enum class TestPhase { COUNTDOWN, FOREPERIOD, STIMULUS, FEEDBACK, FINISHED }

/**
 * Накопленные результаты попыток.
 *
 * @param presented сколько сигналов предъявлено (завершённых попыток).
 */
data class TrialResults(
    val presented: Int = 0,
    val reactionTimesMs: List<Double> = emptyList(),
    val wrongResponses: Int = 0,
    val omissions: Int = 0,
    val prematureResponses: Int = 0,
) {
    val errors: Int get() = wrongResponses + omissions + prematureResponses

    fun withCorrect(reactionTimeMs: Double) =
        copy(presented = presented + 1, reactionTimesMs = reactionTimesMs + reactionTimeMs)

    fun withWrong() = copy(presented = presented + 1, wrongResponses = wrongResponses + 1)

    fun withOmission() = copy(presented = presented + 1, omissions = omissions + 1)

    /** Реакция на предъявленный сигнал, но быстрее порога упреждения — попытка израсходована. */
    fun withAnticipation() = copy(presented = presented + 1, prematureResponses = prematureResponses + 1)

    /** Нажатие до сигнала — попытка НЕ израсходована, интервал до сигнала начинается заново. */
    fun withFalseStart() = copy(prematureResponses = prematureResponses + 1)

    /** Сигнал предъявлен, реакция не требовалась и не была выполнена (правильное торможение). */
    fun withCorrectRejection() = copy(presented = presented + 1)
}

fun TestDTO.withResults(results: TrialResults): TestDTO = copy(
    reactionTimesMs = results.reactionTimesMs,
    errorsCount = results.errors,
    wrongResponses = results.wrongResponses,
    omissions = results.omissions,
    prematureResponses = results.prematureResponses,
    averageValue = ReactionStatistics.meanSeconds(results.reactionTimesMs),
    stdDeviationValue = ReactionStatistics.sampleStdDeviationSeconds(results.reactionTimesMs)
)

/** Случайный интервал до сигнала, равномерно распределённый в [start; finish], мс. */
fun randomForeperiodMs(interval: Interval): Long =
    if (interval.finish > interval.start) Random.nextLong(interval.start, interval.finish + 1) else interval.start

/**
 * Момент предъявления текущего сигнала.
 *
 * Началом сигнала считается первая отрисовка кадра со стимулом (см. reportFirstDraw),
 * а не момент изменения состояния: между ними проходит до одного кадра.
 */
class StimulusClock {
    var stimulusId: Int = 0
        private set
    private var drawnNanos: Long? = null

    /** Начинает новое предъявление и возвращает его идентификатор. */
    fun newStimulus(): Int {
        stimulusId++
        drawnNanos = null
        return stimulusId
    }

    fun onDrawn(id: Int, nanos: Long) {
        if (id == stimulusId && drawnNanos == null) drawnNanos = nanos
    }

    /**
     * Время реакции в мс или null, если сигнал ещё не был отрисован —
     * тогда нажатие не может быть реакцией на него.
     */
    fun reactionTimeMs(pressNanos: Long): Double? {
        val onset = drawnNanos ?: return null
        return (pressNanos - onset) / 1_000_000.0
    }
}

/** true, если время реакции слишком мало для реакции на сигнал (упреждение). */
fun isAnticipation(reactionTimeMs: Double?): Boolean =
    reactionTimeMs == null || reactionTimeMs < ReactionStatistics.ANTICIPATION_THRESHOLD_MS

/**
 * Сохраняет результат в БД сессий и в Excel.
 * Выполняется в NonCancellable: если испытуемый сразу уйдёт с экрана итогов,
 * запись всё равно завершится.
 *
 * @return результат с присвоенным sessionId.
 */
suspend fun saveTestResult(
    result: TestDTO,
    settings: Settings,
    sessionStorage: SessionStorage,
    workbookStorage: WorkbookStorage
): TestDTO = withContext(NonCancellable + Dispatchers.IO) {
    val now = Calendar.getInstance()
    val testId = requireNotNull(result.testMode?.id) { "Не задан тип теста" }

    val sessionId = sessionStorage.insertOrAbort(
        SessionDTO(
            sessionId = 0,
            userId = result.user.id,
            testId = testId,
            testYear = now.get(Calendar.YEAR),
            // Calendar.MONTH начинается с 0; так же читается при построении графиков
            testMonth = now.get(Calendar.MONTH),
            testDay = now.get(Calendar.DAY_OF_MONTH),
            testHourOfDay24h = now.get(Calendar.HOUR_OF_DAY),
            testMinuteOfHour = now.get(Calendar.MINUTE),
            averageValue = result.averageValue,
            standardDeviation = result.stdDeviationValue,
            count = result.count,
            errors = result.errorsCount ?: 0,
            experience = result.user.experience.toString(),
            userAge = result.user.age.toString(),
            drivingLicenseCategory = result.user.drivingLicenseCategory,
            signalInterval = result.interval
        )
    )
    val saved = result.copy(sessionId = sessionId)

    workbookStorage.createWorkbookIfNotExists(
        folderPath = settings.localFolderToTable,
        dataFormats = settings.dataFormats
    )
    workbookStorage.writeDataToWorkbook(
        testDTO = saved,
        folderPath = settings.localFolderToTable,
        dataFormats = settings.dataFormats
    )
    saved
}
