package tests.rmo_stop_line.store

sealed class Event {
    data object StartInitTimer : Event()

    /** Нажата педаль тормоза; [pressNanos] — System.nanoTime() нажатия. */
    data class OnBrakePressed(val pressNanos: Long) : Event()

    data object BackClicked : Event()

    /** Тренировка окончена, испытуемый начинает зачётную часть. */
    data object StartMainTest : Event()
}
