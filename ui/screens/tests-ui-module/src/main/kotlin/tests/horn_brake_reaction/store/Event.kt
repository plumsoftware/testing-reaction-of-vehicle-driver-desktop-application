package tests.horn_brake_reaction.store

sealed class Event {
    data object StartInitTimer : Event()

    /** Нажата педаль тормоза; [pressNanos] — System.nanoTime() нажатия. */
    data class OnBrakePressed(val pressNanos: Long) : Event()

    data object BackClicked : Event()
}
