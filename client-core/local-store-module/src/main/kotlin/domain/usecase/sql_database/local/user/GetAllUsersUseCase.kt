package domain.usecase.sql_database.local.user

import data.model.regular.user.Gender
import data.model.regular.user.User
import domain.repository.UserRepository

class GetAllUsersUseCase(private val userRepository: UserRepository) {
    suspend fun execute() : List<User> {
        val allUsers = userRepository.getAllUsers().map {
            User(
                id = it.user_id,
                name = it.user_name,
                surname = it.user_surname,
                patronymic = it.user_patronymic ?: "",
                gender = Gender.fromString(it.gender),
                login = it.user_login,
                password = it.user_password
            )
        }
        return allUsers
    }
}