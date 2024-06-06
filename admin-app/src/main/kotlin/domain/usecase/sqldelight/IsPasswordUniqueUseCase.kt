package domain.usecase.sqldelight

import domain.repository.SQLDeLightRepository

class IsPasswordUniqueUseCase(private val sqlDeLightRepository: SQLDeLightRepository) {
    suspend fun execute(password: String): Boolean {
        return !sqlDeLightRepository.getAllPasswords().contains(password)
    }
}