package presentation.authorization.login.store

import domain.model.DrivingLicenseCategory

data class State (
    val login: String = "",
    val password: String = "",
    val experience: Int = 0,
    val count: Int = 0,
    val drivingLicenseCategory: DrivingLicenseCategory = DrivingLicenseCategory.Empty,

    val isPasswordError: Boolean = false,
    val isLoginError: Boolean = false,
    val isExperienceError: Boolean = false,
    val isCountError: Boolean = false,
    val isDrivingLicenseCategoryError: Boolean = false,
)