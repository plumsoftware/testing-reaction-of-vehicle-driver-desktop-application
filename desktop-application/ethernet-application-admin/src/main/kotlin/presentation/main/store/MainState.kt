package presentation.main.store

import data.model.dto.test.TestDTO
import data.model.regular.settings.Settings
import data.model.regular.user.User

data class MainState(
    val testDTO: TestDTO = TestDTO(),
    val settings: Settings = Settings(),
    /** Настройки прочитаны из файла (до этого networkDrive пустой не потому, что диск не выбран). */
    val settingsLoaded: Boolean = false,
    val selectedUser: User = User.empty()
)
