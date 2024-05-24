package domain.model.regular

data class User(
    var id: Int,
    var name: String,
    var surname: String,
    var patronymic: String = "",
    var age: Int,
    var gender: Gender,
    var drivingLicenseCategory: DrivingLicenseCategory
) {
    constructor(id: Int, name: String, surname: String, age: Int, gender: Gender, drivingLicenseCategory: DrivingLicenseCategory) :
            this(id, name, surname, "", age, gender, drivingLicenseCategory)

    companion object {
        fun empty () = User(
            id = -1,
            name = "",
            surname = "",
            patronymic = "",
            age = 0,
            gender = Gender.MALE,
            DrivingLicenseCategory.NoDrivingLicense
        )
    }
}