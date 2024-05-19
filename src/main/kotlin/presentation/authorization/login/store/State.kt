package presentation.authorization.login.store

import domain.model.regular.DrivingLicenseCategory
import domain.model.regular.Interval

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
    val isIntervalError: Boolean = false,

    val intervals: List<Interval> = emptyList(),
    val selectedInterval: Interval = Interval(),

//    region::Privacy policy
    val isEnableStartTest: Boolean = false,
//    endregion
)