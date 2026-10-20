package data.model.dto.test

import data.model.regular.user.Interval
import data.model.regular.tests.TestMode
import data.model.regular.user.User

/**
 * Данные одной тестовой сессии.
 *
 * @param count количество предъявленных сигналов (попыток).
 * @param reactionTimesMs время реакции по каждой правильной реакции, мс (в порядке предъявления).
 * @param errorsCount всего ошибок = [wrongResponses] + [omissions] + [prematureResponses].
 * @param wrongResponses реакция на неверный сигнал / нажатие не той кнопки.
 * @param omissions пропуск сигнала: нужная реакция не выполнена за отведённое время.
 * @param prematureResponses фальстарт: нажатие до сигнала или быстрее
 * [ReactionStatistics.ANTICIPATION_THRESHOLD_MS] после него.
 * @param averageValue среднее время правильных реакций, с.
 * @param stdDeviationValue выборочное стандартное отклонение времени правильных реакций, с.
 */
data class TestDTO(
    val user: User = User.empty(),
    val count: Int = 0,
    val interval: Interval = Interval(),

    val testMode: TestMode? = null,
    val reactionTimesMs: List<Double> = emptyList(),
    val errorsCount: Int? = null,
    val wrongResponses: Int = 0,
    val omissions: Int = 0,
    val prematureResponses: Int = 0,
    val sessionId: Long = 0L,
    val averageValue: Double = 0.0,
    val stdDeviationValue: Double = 0.0
)
