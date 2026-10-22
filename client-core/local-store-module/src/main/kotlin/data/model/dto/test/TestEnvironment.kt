package data.model.dto.test

/**
 * Рабочее место, на котором проходил тест. Пустая строка / null — значение получить не удалось.
 *
 * @param monitorSizeCm физический размер изображения монитора «ширина × высота», см (из EDID).
 * @param screenResolution разрешение экрана в физических пикселях.
 * @param scalePercent масштаб интерфейса ОС, %.
 * @param testWindowPx размер окна теста в физических пикселях.
 */
data class TestEnvironment(
    val monitorName: String = "",
    val monitorSizeCm: String = "",
    val monitorDiagonalInches: Double? = null,
    val screenResolution: String = "",
    val refreshRateHz: Int? = null,
    val scalePercent: Int? = null,
    val testWindowPx: String = "",
    val computerName: String = "",
    val operatingSystem: String = "",
    val appVersion: String = "",
    val methodVersion: String = "",
)
