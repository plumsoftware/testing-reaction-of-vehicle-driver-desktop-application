package generate_code.store

data class State(
    val code: String? = null,
    val keySize: String? = "128",
    val number: String? = "",
    val enabled: Boolean = true,
    val isError: Boolean = false
)
