package tests.pvt_brake_lights.store

sealed class Event {
    data object StartInitTimer : Event()

    /** Нажата педаль тормоза; [pressNanos] — System.nanoTime() нажатия. */
    data class OnBrakePressed(val pressNanos: Long) : Event()

    /** Стоп-сигналы впереди идущей машины впервые отрисованы. */
    data class OnStimulusDrawn(val stimulusId: Int, val drawNanos: Long) : Event()

    data object BackClicked : Event()

    /** Тренировка окончена, испытуемый начинает зачётную часть. */
    data object StartMainTest : Event()
}
