package newuser.login.store

import data.model.regular.user.DrivingLicenseCategory
import data.model.dto.test.ResponseHand
import data.model.dto.test.VisionCorrection

sealed class Event {

    data object BackClicked : Event()
    data object StartTest : Event()

    data class OnLoginChanged(val login: String) : Event()
    data class OnPasswordChanged(val password: String) : Event()
    data class OnExperienceChanged(val experience: Int) : Event()
    data class OnDrivingLicenseCategoryChanged(val drivingLicenseCategory: DrivingLicenseCategory) : Event()

    data class OnAgeChanged(val age: Int) : Event()

    data class OnVisionCorrectionChanged(val visionCorrection: VisionCorrection) : Event()
    data class OnSleepHoursChanged(val sleepHours: Int?) : Event()
    data class OnWellBeingChanged(val wellBeing: Int) : Event()
    data class OnResponseHandChanged(val responseHand: ResponseHand) : Event()
}