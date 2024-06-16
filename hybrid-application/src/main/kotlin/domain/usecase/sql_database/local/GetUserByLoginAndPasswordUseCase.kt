package domain.usecase.sql_database.local

import domain.model.either.LocalEither
import domain.model.either.error.NoSuchUserError
import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Gender
import domain.model.regular.user.User
import domain.repository.UserRepository
import ru.plumsoftware.users.Users

class GetUserByLoginAndPasswordUseCase(private val userRepository: UserRepository) {
    suspend fun execute(login: String, password: String): LocalEither<Exception, User> {

        val userByLoginAndPassword: LocalEither<Exception, List<Users>> =
            userRepository.getUserByLoginAndPassword(login = login, password = password)

        when (userByLoginAndPassword) {
            is LocalEither.Data -> {
                if (userByLoginAndPassword.b.isNotEmpty()) {
                    val users: List<User> = userByLoginAndPassword.b.map {
                        User(
                            id = it.user_id,
                            name = it.user_name,
                            surname = it.user_surname,
                            patronymic = it.user_patronymic ?: "",
                            age = 0,
                            gender = Gender.fromString(it.gender),
                            drivingLicenseCategory = DrivingLicenseCategory.Empty,
                            experience = 0,
                            login = it.user_login,
                            password = it.user_password
                        )
                    }
                    return LocalEither.Data(b = users[0])
                } else {
                    return LocalEither.Exception(a = NoSuchUserError())
                }
            }

            is LocalEither.Exception -> {
                return LocalEither.Exception(a = userByLoginAndPassword.a)
            }
        }
    }
}