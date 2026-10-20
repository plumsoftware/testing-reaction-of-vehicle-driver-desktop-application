package data.model.dto.test

/**
 * Показатель, специфичный для теста (например, число провалов в PVT или средняя ошибка в РДО).
 *
 * @param key стабильный идентификатор для анализа (латиница), не меняется при переводе названий.
 * @param name название для людей.
 * @param unit единица измерения («мс», «1/с», «%», «шт.»).
 */
data class TestMetric(
    val key: String,
    val name: String,
    val value: Double,
    val unit: String,
)
