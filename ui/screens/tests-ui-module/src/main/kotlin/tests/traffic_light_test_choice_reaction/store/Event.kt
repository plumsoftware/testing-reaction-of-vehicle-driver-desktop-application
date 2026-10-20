package tests.traffic_light_test_choice_reaction.store

sealed class Event {
    data object BackCLicked : Event()

    /** Нажата кнопка лампы [lampIndex]; [pressNanos] — System.nanoTime() нажатия. */
    data class OnLampButtonPressed(val lampIndex: Int, val pressNanos: Long) : Event()

    /** Горящая лампа впервые отрисована. */
    data class OnStimulusDrawn(val stimulusId: Int, val drawNanos: Long) : Event()

    data object StartTimer : Event()
}
