package domain.usecase.sql_database.roaming

import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Gender
import domain.model.regular.user.User
import domain.repository.UserRepository
import ru.plumsoftware.users.Users

class GetUserByLoginAndPasswordUseCase(private val userRepository: UserRepository) {
    suspend fun execute(login: String, password: String): User {
        val userByLoginAndPassword: List<Users> =
            userRepository.getUserByLoginAndPassword(login = login, password = password)



        val users: List<User> = userByLoginAndPassword.map {
            User(
                id = it.user_id,
                name = it.user_name,
                surname = it.user_surname,
                patronymic = it.user_patronymic ?: "",
                age = it.age.toInt(),
                gender = Gender.fromString(it.gender),
                drivingLicenseCategory = DrivingLicenseCategory.fromString(it.driving_license_category),
                experience = it.experience.toInt()
            )
        }
        return users[0]
    }
}