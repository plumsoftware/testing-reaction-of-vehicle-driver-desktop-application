package tests.car_stop_by_red_light_simple_reaction.store

sealed class Event {
    data object StartInitTimer : Event()

    /** Нажата педаль тормоза; [pressNanos] — System.nanoTime() нажатия. */
    data class OnBrakePressed(val pressNanos: Long) : Event()

    /** Красный сигнал впервые отрисован. */
    data class OnStimulusDrawn(val stimulusId: Int, val drawNanos: Long) : Event()

    data object BackClicked : Event()

    /** Тренировка окончена, испытуемый начинает зачётную часть. */
    data object StartMainTest : Event()
}
