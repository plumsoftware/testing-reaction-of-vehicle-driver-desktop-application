package domain.model.regular.user

data class User(
    var id: Long,
    var name: String,
    var surname: String,
    var patronymic: String = "",
    var age: Int,
    var gender: Gender,
    var drivingLicenseCategory: DrivingLicenseCategory,
    var experience: Int
) {

    constructor(id: Long, name: String, surname: String, patronymic: String, gender: Gender) :
            this(id, name, surname, patronymic, 0, gender, DrivingLicenseCategory.Empty, 0)


    constructor(name: String, surname: String, patronymic: String, gender: Gender) :
            this(0L, name, surname, patronymic, 0, gender, DrivingLicenseCategory.Empty, 0)

    companion object {
        fun empty () = User(
            id = -1L,
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