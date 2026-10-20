package tests.common

import data.model.dto.database.SessionDTO
import data.model.dto.test.ReactionStatistics
import data.model.dto.test.TestDTO
import data.model.dto.test.TrialOutcome
import data.model.dto.test.TrialRecord
import data.model.regular.settings.Settings
import data.model.regular.user.Interval
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import utlis.EnvironmentCollector
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
 * Попытки теста по порядку; все итоговые показатели считаются из этого списка.
 */
data class TrialResults(val trials: List<TrialRecord> = emptyList()) {

    /** Сколько сигналов предъявлено (фальстарты до сигнала не считаются). */
    val presented: Int get() = trials.count { it.outcome != TrialOutcome.FALSE_START }

    val reactionTimesMs: List<Double>
        get() = trials.filter { it.outcome == TrialOutcome.CORRECT }.mapNotNull { it.reactionTimeMs }

    val wrongResponses: Int get() = trials.count { it.outcome == TrialOutcome.WRONG }
    val omissions: Int get() = trials.count { it.outcome == TrialOutcome.OMISSION }
    val prematureResponses: Int
        get() = trials.count { it.outcome == TrialOutcome.FALSE_START || it.outcome == TrialOutcome.ANTICIPATION }

    val errors: Int get() = wrongResponses + omissions + prematureResponses

    /** Номер попытки, которая сейчас идёт или ожидается. */
    val currentTrialNumber: Int get() = presented + 1

    fun with(
        stimulus: String?,
        foreperiodMs: Long,
        outcome: TrialOutcome,
        reactionTimeMs: Double? = null
    ) = copy(
        trials = trials + TrialRecord(
            number = currentTrialNumber,
            stimulus = stimulus,
            foreperiodMs = foreperiodMs,
            outcome = outcome,
            reactionTimeMs = reactionTimeMs
        )
    )
}

fun TestDTO.withResults(results: TrialResults): TestDTO = copy(
    reactionTimesMs = results.reactionTimesMs,
    errorsCount = results.errors,
    wrongResponses = results.wrongResponses,
    omissions = results.omissions,
    prematureResponses = results.prematureResponses,
    averageValue = ReactionStatistics.meanSeconds(results.reactionTimesMs),
    stdDeviationValue = ReactionStatistics.sampleStdDeviationSeconds(results.reactionTimesMs),
    trials = results.trials
)

/**
 * Случайный интервал до сигнала и момент его начала: нужен, чтобы для фальстарта записать,
 * на сколько раньше запланированного сигнала было нажатие.
 */
class Foreperiod(val durationMs: Long) {
    private val startNanos = System.nanoTime()

    /** Отрицательное время (мс) от запланированного сигнала до нажатия [pressNanos]. */
    fun msBeforeSignal(pressNanos: Long): Double =
        (pressNanos - startNanos) / 1_000_000.0 - durationMs
}

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

    // Какой по счёту раз пользователь проходит этот тест (для учёта натренированности)
    val previousSessions = sessionStorage.getSessionsWithUserId(userId = result.user.id)
        .count { it.testId == testId }
    val enriched = result.copy(
        userSessionNumber = previousSessions + 1,
        environment = EnvironmentCollector.collect()
    )

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
            signalInterval = result.interval,
            validCount = enriched.reactionTimesMs.size,
            wrongResponses = enriched.wrongResponses,
            omissions = enriched.omissions,
            prematureResponses = enriched.prematureResponses,
            handedness = enriched.user.handedness,
            userSessionNumber = enriched.userSessionNumber,
            preTestState = enriched.preTestState,
            environment = enriched.environment
        ),
        trials = enriched.trials
    )
    val saved = enriched.copy(sessionId = sessionId)

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

/** Название сигнала светофора по индексу лампы: 0 — красный, 1 — жёлтый, 2 — зелёный. */
fun lampName(lampIndex: Int): String = when (lampIndex) {
    0 -> "красный"
    1 -> "жёлтый"
    2 -> "зелёный"
    else -> "—"
}
