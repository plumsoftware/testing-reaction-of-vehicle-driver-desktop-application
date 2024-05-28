package domain.model.regular.user

data class User(
    var id: Int,
    var name: String,
    var surname: String,
    var patronymic: String = "",
    var age: Int,
    var gender: Gender,
    var drivingLicenseCategory: DrivingLicenseCategory,
    var experience: Int
) {
    constructor(id: Int, name: String, surname: String, age: Int, gender: Gender, drivingLicenseCategory: DrivingLicenseCategory, experience: Int) :
            this(id, name, surname, "", age, gender, drivingLicenseCategory, experience)

    companion object {
        fun empty () = User(
            id = -1,
            name = "",
            surname = "",
            patronymic = "",
            age = 0,
            gender = Gender.MALE,
            drivingLicenseCategory = DrivingLicenseCategory.NoDrivingLicense,
            experience = 0
        )
    }
}