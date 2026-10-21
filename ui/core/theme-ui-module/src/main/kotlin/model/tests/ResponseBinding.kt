package model.tests

import androidx.compose.ui.input.key.Key

/**
 * Ответ в тесте: клавиша при ответе с клавиатуры и элемент на экране при ответе мышью.
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
)

object ResponseKeys {
    val BRAKE = ResponseBinding(action = "Тормоз", key = Key.Spacebar, keyLabel = "Пробел", mouseTarget = "педаль тормоза")
    val GAS = ResponseBinding(action = "Газ", key = Key.Spacebar, keyLabel = "Пробел", mouseTarget = "педаль газа")

    /** Сигналы светофора по индексу лампы: 0 — красный, 1 — жёлтый, 2 — зелёный. */
    val LAMPS = listOf(
        ResponseBinding(action = "Красный", key = Key.DirectionLeft, keyLabel = "←", mouseTarget = "красная кнопка"),
        ResponseBinding(action = "Жёлтый", key = Key.DirectionDown, keyLabel = "↓", mouseTarget = "жёлтая кнопка"),
        ResponseBinding(action = "Зелёный", key = Key.DirectionRight, keyLabel = "→", mouseTarget = "зелёная кнопка"),
    )
}
