package data.model.dto.test

/** Коррекция зрения во время теста. */
enum class VisionCorrection(private val title: String) {
    NONE("Нет"),
    GLASSES("Очки"),
    CONTACT_LENSES("Контактные линзы");

    override fun toString(): String = title
}

/** Рука, которой испытуемый отвечает в тесте. */
enum class ResponseHand(private val title: String) {
    RIGHT("Правая"),
    LEFT("Левая");

    override fun toString(): String = title
}

/** Устройство, которым испытуемый отвечает в тесте. */
enum class InputDevice(private val title: String) {
    MOUSE("Мышь"),
    TOUCHPAD("Тачпад"),
    OTHER("Другое");

    override fun toString(): String = title
}

/**
 * Состояние испытуемого перед тестом (анкета на экране входа).
 *
 * @param sleepHours часов сна за последние сутки.
 * @param wellBeing самочувствие по шкале 1 (плохое) – 5 (отличное).
 */
data class PreTestState(
    val visionCorrection: VisionCorrection? = null,
    val sleepHours: Int? = null,
    val wellBeing: Int? = null,
    val responseHand: ResponseHand? = null,
    val inputDevice: InputDevice? = null,
) {
    val isComplete: Boolean
        get() = visionCorrection != null && sleepHours != null && wellBeing != null &&
                responseHand != null && inputDevice != null

    companion object {
        val sleepHoursRange = 0..24
        val wellBeingScale = 1..5
    }
}
