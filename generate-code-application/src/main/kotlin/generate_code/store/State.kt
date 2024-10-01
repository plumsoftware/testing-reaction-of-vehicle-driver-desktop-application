package generate_code.store

data class State(
    val code: String? = null,
    val keySize: String? = "128",
    val enabled: Boolean = true,
    val isError: Boolean = false
)
