package domain.model.regular.tests

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