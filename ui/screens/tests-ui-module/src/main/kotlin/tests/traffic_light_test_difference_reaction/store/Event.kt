package tests.traffic_light_test_difference_reaction.store

sealed class Event {
    data object BackCLicked : Event()

    /** Нажата педаль газа; [pressNanos] — System.nanoTime() нажатия. */
    data class OnGasPedalPressed(val pressNanos: Long) : Event()

    /** Горящая лампа впервые отрисована. */
    data class OnStimulusDrawn(val stimulusId: Int, val drawNanos: Long) : Event()

    data object StartTimer : Event()

    /** Тренировка окончена, испытуемый начинает зачётную часть. */
    data object StartMainTest : Event()
}
