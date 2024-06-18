package presentation.authorization.login.store

import domain.model.dto.TestDTO

sealed class Output {
    data object BackButtonClicked : Output()
    data class OpenTestMenu(val testDTO: TestDTO) : Output()
    data object OpenPrivacyPolicy : Output()
}