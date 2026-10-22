package data.model.regular.user

/** Ведущая рука испытуемого. */
enum class Handedness(private val title: String) {
    UNKNOWN(""),
    RIGHT("Правша"),
    LEFT("Левша"),
    AMBIDEXTROUS("Амбидекстр");

    override fun toString(): String = title

    companion object {
        fun fromString(value: String): Handedness = entries.find { it.title == value } ?: UNKNOWN

        /** Варианты для выбора в интерфейсе. */
        val selectable: List<Handedness> = listOf(RIGHT, LEFT, AMBIDEXTROUS)
    }
}
