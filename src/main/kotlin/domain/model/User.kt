package domain.model

class User(
    var name: String,
    var surname: String,
    var patronymic: String = "",
    var age: Int,
    var gender: Gender,
    var drivingLicenseCategory: DrivingLicenseCategory
) {
    constructor(name: String, surname: String, age: Int, gender: Gender, drivingLicenseCategory: DrivingLicenseCategory) :
            this(name, surname, "", age, gender, drivingLicenseCategory)
}