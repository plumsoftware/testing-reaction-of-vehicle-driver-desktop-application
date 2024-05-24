package domain.model.regular

data class TestMode(
    val name: String?,
    val id: Int?
) {
    companion object {
        fun empty(): TestMode {
            return TestMode(
                name = null,
                id = null
            )
        }
    }
}