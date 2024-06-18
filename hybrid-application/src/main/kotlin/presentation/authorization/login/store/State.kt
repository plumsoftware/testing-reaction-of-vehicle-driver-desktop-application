package presentation.authorization.login.store

import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Interval

data class State (
    val login: String = "",
    val password: String = "",
    val experience: Int = 0,
    val count: Int = 0,
    val drivingLicenseCategory: DrivingLicenseCategory = DrivingLicenseCategory.Empty,
    val age: Int = -1,

    val isPasswordError: Boolean = false,
    val isLoginError: Boolean = false,
    val isExperienceError: Boolean = false,
    val isCountError: Boolean = false,
    val isDrivingLicenseCategoryError: Boolean = false,
    val isIntervalError: Boolean = false,
    val isAgeError: Boolean = false,

    val intervals: List<Interval> = emptyList(),
    val selectedInterval: Interval = Interval(),

    val roamingErrorMessage: String = "",

//    region::Privacy policy
    val isEnableStartTest: Boolean = false,
//    endregion
)