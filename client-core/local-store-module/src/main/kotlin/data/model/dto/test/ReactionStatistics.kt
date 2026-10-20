package data.model.dto.test

import kotlin.math.sqrt

object ReactionStatistics {
    /**
     * Реакция быстрее этого порога после сигнала физиологически невозможна
     * и считается упреждающей (фальстартом), а не реакцией на сигнал.
     */
    const val ANTICIPATION_THRESHOLD_MS = 100.0

    /** Среднее время реакции в секундах; 0.0, если правильных реакций нет. */
    fun meanSeconds(reactionTimesMs: List<Double>): Double {
        if (reactionTimesMs.isEmpty()) return 0.0
        return reactionTimesMs.average() / 1000.0
    }

    /**
     * Выборочное (несмещённое, n − 1) стандартное отклонение в секундах;
     * 0.0, если реакций меньше двух.
     */
    fun sampleStdDeviationSeconds(reactionTimesMs: List<Double>): Double {
        if (reactionTimesMs.size < 2) return 0.0
        val mean = reactionTimesMs.average()
        val variance = reactionTimesMs.sumOf { (it - mean) * (it - mean) } / (reactionTimesMs.size - 1)
        return sqrt(variance) / 1000.0
    }
}
