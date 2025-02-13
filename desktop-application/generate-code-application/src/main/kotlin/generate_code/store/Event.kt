package generate_code.store

sealed class Event {
    data object GenerateCode : Event()
    data class ChangeKeySize(val keySize: String) : Event()
}