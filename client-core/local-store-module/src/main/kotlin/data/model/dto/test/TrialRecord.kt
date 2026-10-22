package data.model.dto.test

/** Исход одной попытки. */
enum class TrialOutcome(val title: String) {
    /** Правильная реакция на сигнал — время реакции учитывается в статистике. */
    CORRECT("верная реакция"),

    /** Сигнал, на который не нужно реагировать, и реакции не было. */
    CORRECT_REJECTION("верно: без реакции"),

    WRONG("неверная реакция"),
    OMISSION("пропуск сигнала"),

    /** Реакция быстрее порога упреждения после сигнала. */
    ANTICIPATION("фальстарт: упреждение"),

    /** Нажатие до сигнала; попытка не засчитывается и повторяется. */
    FALSE_START("фальстарт: до сигнала");

    override fun toString(): String = title
}

/**
 * Одна попытка теста.
 *
 * @param number номер попытки (фальстарт до сигнала получает номер попытки, которая ожидалась).
 * @param stimulus предъявленный сигнал; null — сигнала ещё не было (фальстарт до сигнала).
 * @param foreperiodMs запланированный интервал до сигнала, мс.
 * @param reactionTimeMs время от сигнала до нажатия, мс; для фальстарта до сигнала —
 * отрицательное: на сколько раньше запланированного сигнала было нажатие; null — нажатия не было.
 */
data class TrialRecord(
    val number: Int,
    val stimulus: String?,
    val foreperiodMs: Long,
    val outcome: TrialOutcome,
    val reactionTimeMs: Double?,
)
