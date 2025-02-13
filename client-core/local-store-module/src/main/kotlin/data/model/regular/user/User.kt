package data.model.regular.user

data class User(
    var id: Long,
    var name: String,
    var surname: String,
    var patronymic: String = "",
    var age: Int,
    var gender: Gender,
    var drivingLicenseCategory: DrivingLicenseCategory,
    var experience: Int,
    var login: String,
    var password: String,
) {

    constructor(id: Long, name: String, surname: String, patronymic: String, gender: Gender, login: String, password: String) :
            this(id, name, surname, patronymic, 0, gender, DrivingLicenseCategory.Empty, 0, login, password)


    constructor(name: String, surname: String, patronymic: String, gender: Gender, login: String, password: String) :
            this(0L, name, surname, patronymic, 0, gender, DrivingLicenseCategory.Empty, 0, login, password)

    companion object {
        fun empty() = User(
            id = -1L,
            name = "",
            surname = "",
            patronymic = "",
            age = 0,
            gender = Gender.MALE,
            drivingLicenseCategory = DrivingLicenseCategory.NoDrivingLicense,
            experience = 0,
            login = "",
            password = ""
        )
    }
}