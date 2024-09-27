package authorization.login.store

import data.model.dto.TestDTO

sealed class Output {
    data object BackButtonClicked : Output()
    data class OpenTestMenu(val testDTO: TestDTO) : Output()
}