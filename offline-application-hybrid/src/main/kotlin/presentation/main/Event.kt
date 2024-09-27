package presentation.main

import data.model.dto.test.TestDTO

sealed class Event {
    data class ChangeTestDto(val testDto: TestDTO) : Event()
}