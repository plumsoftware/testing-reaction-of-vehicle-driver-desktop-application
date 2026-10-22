package newuser.login.store

import data.model.regular.user.DrivingLicenseCategory
import data.model.dto.test.PreTestState

data class State (
    val login: String = "",
    val password: String = "",
    val experience: Int = 0,
    val drivingLicenseCategory: DrivingLicenseCategory = DrivingLicenseCategory.Empty,
    val age: Int = -1,

    val isPasswordError: Boolean = false,
    val isLoginError: Boolean = false,
    val isExperienceError: Boolean = false,
    val isDrivingLicenseCategoryError: Boolean = false,
    val isAgeError: Boolean = false,

    /** Анкета «Состояние перед тестом». */
    val preTestState: PreTestState = PreTestState(),
    /** Подсвечивать незаполненные поля анкеты (после попытки начать тест). */
    val showPreTestErrors: Boolean = false,

    val roamingErrorMessage: String = ""
)