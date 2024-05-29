package domain.usecase.sql_database.local

import domain.model.dto.database.SessionDTO
import domain.repository.SessionRepository

class InsertOrAbortNewSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO) {
        sessionRepository.insertOrAbortNewSession(sessionDTO = sessionDTO)
    }
}