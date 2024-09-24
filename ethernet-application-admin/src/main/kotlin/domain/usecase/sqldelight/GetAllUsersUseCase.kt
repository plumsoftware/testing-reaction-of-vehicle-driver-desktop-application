package domain.usecase.sqldelight

import domain.model.regular.user.Gender
import domain.model.regular.user.User
import domain.repository.SQLDeLightRepository

class GetAllUsersUseCase(private val sqlDeLightRepository: SQLDeLightRepository) {
    suspend fun execute() : List<User> {
        val allUsers = sqlDeLightRepository.getAllUsers().map {
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