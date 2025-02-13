package domain.usecase.sql_database.local.user

import domain.repository.UserRepository

class IsPasswordUniqueUseCase(private val userRepository: UserRepository) {
    suspend fun execute(password: String): Boolean {
        return !userRepository.getAllPasswords().contains(password)
    }
}