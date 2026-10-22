package model.tests

import androidx.compose.ui.input.key.Key
import data.model.dto.test.InputDevice
import data.model.regular.settings.Pedal
import data.model.regular.settings.PedalBindings

/**
 * Ответ в тесте: клавиша при ответе с клавиатуры, педаль при ответе педалями
 * и элемент на экране при ответе мышью.
 * Клавиши выбраны так, чтобы пальцы лежали на них весь тест без перемещения руки.
 */
data class ResponseBinding(
    /** Что означает ответ: «торможение», «красный сигнал». */
    val action: String,
    val key: Key,
    /** Подпись клавиши на экране. */
    val keyLabel: String,
    /** Куда нажимать мышью. */
    val mouseTarget: String,
    /** Педаль для этого ответа; null — педалями так не ответить (например, выбор сигнала светофора). */
    val pedal: Pedal? = null,
)

object ResponseKeys {
    val BRAKE = ResponseBinding(
        action = "Тормоз", key = Key.Spacebar, keyLabel = "Пробел", mouseTarget = "педаль тормоза", pedal = Pedal.BRAKE
    )
    val GAS = ResponseBinding(
        action = "Газ", key = Key.Spacebar, keyLabel = "Пробел", mouseTarget = "педаль газа", pedal = Pedal.GAS
    )

    /** Сигналы светофора по индексу лампы: 0 — красный, 1 — жёлтый, 2 — зелёный. */
    val LAMPS = listOf(
        ResponseBinding(action = "Красный", key = Key.DirectionLeft, keyLabel = "←", mouseTarget = "красная кнопка"),
        ResponseBinding(action = "Жёлтый", key = Key.DirectionDown, keyLabel = "↓", mouseTarget = "жёлтая кнопка"),
        ResponseBinding(action = "Зелёный", key = Key.DirectionRight, keyLabel = "→", mouseTarget = "зелёная кнопка"),
    )
}

/** Педалями можно пройти тест, только если у каждого ответа есть педаль. */
val ReactionTest.supportsPedals: Boolean
    get() = responses.all { it.pedal != null }

/** Педали, которые нужны тесту, но ещё не назначены в настройках. */
fun ReactionTest.unassignedPedals(pedals: PedalBindings): List<Pedal> =
    responses.mapNotNull { it.pedal }.distinct().filter { pedals[it] == null }

/**
 * Как испытуемый отвечает: клавиатура и педали — клавишами (мышь не работает), иначе — мышью.
 * Педали посылают клавиши, назначенные в настройках ([PedalBindings]).
 */
class ResponseInput(val device: InputDevice?, private val pedals: PedalBindings) {

    /** Ответ клавишами (клавиатура или педали), мышь игнорируется. */
    val usesKeys: Boolean get() = device == InputDevice.KEYBOARD || device == InputDevice.PEDALS

    /** Клавиша для ответа; null — ответ не клавишей (мышь) или педаль не назначена. */
    fun keyOf(binding: ResponseBinding): Key? = when (device) {
        InputDevice.KEYBOARD -> binding.key
        InputDevice.PEDALS -> binding.pedal?.let { pedals[it] }?.let { Key(it.keyCode) }
        else -> null
    }

    /** Подпись ответа: клавиша или педаль. */
    fun label(binding: ResponseBinding): String =
        if (device == InputDevice.PEDALS) binding.mouseTarget else binding.keyLabel

    /** Подсказка над сценой: «нажмите «Пробел»» или «нажмите педаль тормоза». */
    fun hint(binding: ResponseBinding): String =
        if (device == InputDevice.KEYBOARD) "нажмите «${binding.keyLabel}»" else "нажмите ${binding.mouseTarget}"
}
