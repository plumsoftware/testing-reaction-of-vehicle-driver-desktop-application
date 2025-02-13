package data.model.regular.tests

data class TestMode(
    val name: String?,
    val id: Long?
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