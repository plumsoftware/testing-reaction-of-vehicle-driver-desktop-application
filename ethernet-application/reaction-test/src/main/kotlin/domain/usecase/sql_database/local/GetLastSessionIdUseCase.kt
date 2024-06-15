package domain.usecase.sql_database.local

import domain.repository.SessionRepository

class GetLastSessionIdUseCase(private val sessionRepository: SessionRepository) {
    suspend fun execute() : Long {
        return sessionRepository.getLastSessionId()
    }
}