package presentation.main.store

import data.model.dto.test.TestDTO

data class MainState(
    val testDTO: TestDTO = TestDTO()
)
