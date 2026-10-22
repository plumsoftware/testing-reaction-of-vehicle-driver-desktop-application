package model.tests

/** Тренировочные попытки перед зачётной частью теста; их результаты никуда не записываются. */
const val PRACTICE_TRIALS = 5

/** Картинки из ресурсов theme-ui-module для слайдов обучения. */
object TestIllustrations {
    const val TRAFFIC_LIGHT = "traffic_light.png"
    const val CAR = "car_top_side_view.png"
    const val GAS_PEDAL = "gas_pedal.png"
    const val STOP_PEDAL = "stop_pedal.png"
}

interface ReactionTest {
    val id: Int
    val name: String
    val route: String

    /** Что происходит на экране и на что реагировать — без привязки к устройству ввода. */
    val instruction: String

    /** Что считается ошибкой. */
    val errorsDescription: String

    /** Варианты ответа: клавиши и элементы на экране для мыши. */
    val responses: List<ResponseBinding>

    /** Картинки для слайда «Что будет на экране». */
    val illustrations: List<String>

    /**
     * Длительность и интервалы заданы методикой теста (PVT), поэтому в окне перед запуском
     * количество попыток и интервал сигнала не выбираются.
     */
    val hasFixedProtocol: Boolean get() = false
}
