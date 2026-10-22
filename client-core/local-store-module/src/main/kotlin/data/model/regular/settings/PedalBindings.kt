package data.model.regular.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Педаль, на которую назначается клавиша. */
enum class Pedal(private val title: String) {
    BRAKE("Тормоз"),
    GAS("Газ");

    override fun toString(): String = title
}

/**
 * Клавиша, которую посылает педаль (USB-педали, эмулирующие клавиатуру).
 *
 * @param keyCode код клавиши Compose (`Key.keyCode`).
 * @param label название клавиши для показа на экране.
 */
@Serializable
data class PedalKey(
    @SerialName("keyCode") val keyCode: Long,
    @SerialName("label") val label: String,
)

/** Назначение клавиш педалям; null — педаль не назначена. */
@Serializable
data class PedalBindings(
    @SerialName("brake") val brake: PedalKey? = null,
    @SerialName("gas") val gas: PedalKey? = null,
) {
    operator fun get(pedal: Pedal): PedalKey? = when (pedal) {
        Pedal.BRAKE -> brake
        Pedal.GAS -> gas
    }

    fun with(pedal: Pedal, key: PedalKey?): PedalBindings = when (pedal) {
        Pedal.BRAKE -> copy(brake = key)
        Pedal.GAS -> copy(gas = key)
    }
}
