package domain.usecase.sqldelight

import domain.model.regular.user.User
import domain.repository.SQLDeLightRepository

class UpdateUserUseCase(private val sqlDeLightRepository: SQLDeLightRepository) {
    suspend fun execute(user: User, login: String, password: String) {
        sqlDeLightRepository.updateUser(user = user, login = login, password = password)
    }
}