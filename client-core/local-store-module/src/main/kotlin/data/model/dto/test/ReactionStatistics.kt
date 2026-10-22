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

    fun median(values: List<Double>): Double? {
        if (values.isEmpty()) return null
        val sorted = values.sorted()
        val middle = sorted.size / 2
        return if (sorted.size % 2 == 1) sorted[middle] else (sorted[middle - 1] + sorted[middle]) / 2
    }

    /**
     * Среднее самых быстрых (или самых медленных) [fraction] значений.
     * Берётся не меньше одного значения: ceil(n × fraction).
     */
    fun meanOfFastest(values: List<Double>, fraction: Double = 0.1): Double? {
        if (values.isEmpty()) return null
        val k = maxOf(1, kotlin.math.ceil(values.size * fraction).toInt())
        return values.sorted().take(k).average()
    }

    fun meanOfSlowest(values: List<Double>, fraction: Double = 0.1): Double? {
        if (values.isEmpty()) return null
        val k = maxOf(1, kotlin.math.ceil(values.size * fraction).toInt())
        return values.sortedDescending().take(k).average()
    }

    /** Коэффициент вариации, % (выборочное SD / среднее); null при n < 2. */
    fun coefficientOfVariationPercent(values: List<Double>): Double? {
        if (values.size < 2) return null
        val mean = values.average()
        if (mean == 0.0) return null
        val sd = sqrt(values.sumOf { (it - mean) * (it - mean) } / (values.size - 1))
        return sd / mean * 100
    }

    /**
     * Чувствительность d′ и критерий c теории обнаружения сигнала с поправкой log-linear
     * (Hautus, 1995): H = (hits + 0,5) / (signals + 1), F = (falseAlarms + 0,5) / (noise + 1).
     * Поправка исключает бесконечные значения при 0 % или 100 %.
     *
     * @return пара (d′, c) или null, если нет проб одного из типов.
     */
    fun dPrimeAndCriterion(hits: Int, signals: Int, falseAlarms: Int, noise: Int): Pair<Double, Double>? {
        if (signals == 0 || noise == 0) return null
        val hitRate = (hits + 0.5) / (signals + 1)
        val falseAlarmRate = (falseAlarms + 0.5) / (noise + 1)
        val zHit = inverseNormalCdf(hitRate)
        val zFalseAlarm = inverseNormalCdf(falseAlarmRate)
        return (zHit - zFalseAlarm) to (-(zHit + zFalseAlarm) / 2)
    }

    /**
     * Обратная функция стандартного нормального распределения (алгоритм P. J. Acklam,
     * относительная погрешность < 1,15e-9).
     */
    fun inverseNormalCdf(p: Double): Double {
        require(p > 0.0 && p < 1.0) { "p должно быть в (0; 1)" }
        val a = doubleArrayOf(
            -3.969683028665376e+01, 2.209460984245205e+02, -2.759285104469687e+02,
            1.383577518672690e+02, -3.066479806614716e+01, 2.506628277459239e+00
        )
        val b = doubleArrayOf(
            -5.447609879822406e+01, 1.615858368580409e+02, -1.556989798598866e+02,
            6.680131188771972e+01, -1.328068155288572e+01
        )
        val c = doubleArrayOf(
            -7.784894002430293e-03, -3.223964580411365e-01, -2.400758277161838e+00,
            -2.549732539343734e+00, 4.374664141464968e+00, 2.938163982698783e+00
        )
        val d = doubleArrayOf(
            7.784695709041462e-03, 3.224671290700398e-01, 2.445134137142996e+00,
            3.754408661907416e+00
        )
        val pLow = 0.02425
        val pHigh = 1 - pLow
        return when {
            p < pLow -> {
                val q = sqrt(-2 * kotlin.math.ln(p))
                (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]) /
                        ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1)
            }

            p <= pHigh -> {
                val q = p - 0.5
                val r = q * q
                (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5]) * q /
                        (((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1)
            }

            else -> {
                val q = sqrt(-2 * kotlin.math.ln(1 - p))
                -(((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]) /
                        ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1)
            }
        }
    }
}
