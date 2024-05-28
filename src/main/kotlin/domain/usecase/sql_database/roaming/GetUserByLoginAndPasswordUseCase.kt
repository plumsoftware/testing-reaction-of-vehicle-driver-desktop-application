package domain.usecase.sql_database.roaming

import domain.model.either.RoamingEither
import domain.model.either.error.NoSuchUserError
import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Gender
import domain.model.regular.user.User
import domain.repository.UserRepository
import ru.plumsoftware.users.Users

class GetUserByLoginAndPasswordUseCase(private val userRepository: UserRepository) {
    suspend fun execute(login: String, password: String): RoamingEither<Exception, User> {

        val userByLoginAndPassword: RoamingEither<Exception, List<Users>> =
            userRepository.getUserByLoginAndPassword(login = login, password = password)

        when (userByLoginAndPassword) {
            is RoamingEither.Data -> {
                if (userByLoginAndPassword.b.isNotEmpty()) {
                    val users: List<User> = userByLoginAndPassword.b.map {
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
                    return RoamingEither.Data(b = users[0])
                } else {
                    return RoamingEither.Exception(a = NoSuchUserError())
                }
            }

            is RoamingEither.Exception -> {
                return RoamingEither.Exception(a = userByLoginAndPassword.a)
            }
        }
    }
}