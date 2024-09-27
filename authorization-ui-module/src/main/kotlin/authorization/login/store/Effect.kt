package authorization.login.store

import data.model.dto.test.TestDTO

sealed class Effect {
    data object BackButtonClicked : Effect()
    data class OpenTestMenu(val testDto: TestDTO) : Effect()
    data object OpenPrivacyPolicy : Effect()
}