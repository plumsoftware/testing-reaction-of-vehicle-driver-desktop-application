package presentation.main.store

import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import data.model.regular.user.User

data class MainState(
    val testDTO: TestDTO = TestDTO(),
    val settings: Settings = Settings(),
    val selectedUser: User = User.empty()
)
