package presentation.main.store

import data.model.dto.test.TestDTO
import data.model.regular.user.User

sealed class Event {
    data class ChangeTestDto(val testDto: TestDTO) : Event()
    data object LoadSettings : Event()
    data class ChangeSelectedUser(val user: User) : Event()
}