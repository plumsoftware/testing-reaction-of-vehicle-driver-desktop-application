package tests.common

import data.model.dto.test.ReactionStatistics
import data.model.dto.test.TestMetric
import data.model.dto.test.TrialOutcome

/**
 * Общие показатели тестов на время реакции (по правильным реакциям):
 * медиана, средние самых быстрых и самых медленных 10 %, коэффициент вариации, доля правильных ответов.
 */
fun reactionTimeMetrics(results: TrialResults): List<TestMetric> {
    val rts = results.reactionTimesMs
    val correctAnswers = results.trials.count {
        it.outcome == TrialOutcome.CORRECT || it.outcome == TrialOutcome.CORRECT_REJECTION
    }
    return buildList {
        ReactionStatistics.median(rts)?.let {
            add(TestMetric("rt_median_ms", "Медиана времени правильной реакции", it, "мс"))
        }
        ReactionStatistics.meanOfFastest(rts)?.let {
            add(TestMetric("rt_fastest10_ms", "Среднее 10 % самых быстрых реакций", it, "мс"))
        }
        ReactionStatistics.meanOfSlowest(rts)?.let {
            add(TestMetric("rt_slowest10_ms", "Среднее 10 % самых медленных реакций", it, "мс"))
        }
        ReactionStatistics.coefficientOfVariationPercent(rts)?.let {
            add(TestMetric("rt_cv_percent", "Коэффициент вариации времени реакции", it, "%"))
        }
        if (results.presented > 0) {
            add(
                TestMetric(
                    "accuracy_percent",
                    "Доля правильных ответов",
                    correctAnswers * 100.0 / results.presented,
                    "%"
                )
            )
        }
    }
}

/**
 * Показатели теории обнаружения сигнала для go/no-go.
 * Попадание — правильное нажатие на разрешающий сигнал; ложная тревога — любое нажатие
 * (включая упреждение) на запрещающий сигнал. Фальстарты до сигнала не входят.
 */
fun goNoGoMetrics(results: TrialResults, goStimulus: String): List<TestMetric> {
    val presented = results.trials.filter { it.outcome != TrialOutcome.FALSE_START }
    val goTrials = presented.filter { it.stimulus == goStimulus }
    val noGoTrials = presented.filter { it.stimulus != goStimulus }
    val hits = goTrials.count { it.outcome == TrialOutcome.CORRECT }
    val falseAlarms = noGoTrials.count { it.outcome == TrialOutcome.WRONG || it.outcome == TrialOutcome.ANTICIPATION }

    val (dPrime, criterion) = ReactionStatistics.dPrimeAndCriterion(
        hits = hits,
        signals = goTrials.size,
        falseAlarms = falseAlarms,
        noise = noGoTrials.size
    ) ?: return emptyList()

    return listOf(
        TestMetric("go_hit_rate_percent", "Доля попаданий (реакция на разрешающий сигнал)", hits * 100.0 / goTrials.size, "%"),
        TestMetric("nogo_false_alarm_rate_percent", "Доля ложных тревог (реакция на запрещающий сигнал)", falseAlarms * 100.0 / noGoTrials.size, "%"),
        TestMetric("d_prime", "Чувствительность d′ (поправка Hautus)", dPrime, ""),
        TestMetric("criterion_c", "Критерий c (> 0 — осторожность, < 0 — импульсивность)", criterion, ""),
    )
}
