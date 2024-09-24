package domain.usecase.sqldelight

import domain.model.regular.user.User
import domain.repository.SQLDeLightRepository

class UpdateUserUseCase(private val sqlDeLightRepository: SQLDeLightRepository) {
    suspend fun execute(user: User) {
        sqlDeLightRepository.updateUser(user = user)
    }
}