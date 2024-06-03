package domain.usecase.sqldelight

import domain.repository.SQLDeLightRepository

class DeleteUserUseCase(private val sqlDeLightRepository: SQLDeLightRepository) {
    suspend fun execute(userId: Long) {
        sqlDeLightRepository.deleteUser(id = userId)
    }
}