package domain.usecase.sql_database.local.user

import domain.repository.UserRepository

class DeleteUserUseCase(private val userRepository: UserRepository) {
    suspend fun execute(userId: Long) {
        userRepository.deleteUser(id = userId)
    }
}