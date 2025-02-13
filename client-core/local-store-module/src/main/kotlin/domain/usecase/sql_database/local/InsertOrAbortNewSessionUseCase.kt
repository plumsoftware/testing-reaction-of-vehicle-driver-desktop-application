package domain.usecase.sql_database.local

import data.model.dto.database.SessionDTO
import domain.repository.SessionRepository

class InsertOrAbortNewSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO) {
        sessionRepository.insertOrAbortNewSession(sessionDTO = sessionDTO)
    }
}