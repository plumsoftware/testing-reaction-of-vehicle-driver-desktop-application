package presentation.users.store

sealed class Output {
    data object BackButtonClicked : Output()

    data class OnUserClicked(val userId: Long) : Output()
}