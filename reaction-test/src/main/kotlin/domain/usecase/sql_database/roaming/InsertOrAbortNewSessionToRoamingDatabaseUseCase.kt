package domain.usecase.sql_database.roaming

import domain.model.dto.database.SessionDTO
import domain.repository.SessionRepository

class InsertOrAbortNewSessionToRoamingDatabaseUseCase(private val sessionRepository: SessionRepository) {
    suspend fun execute(sessionDTO: SessionDTO) {
        sessionRepository.insertOrAbortNewSessionToRoamingDatabase(sessionDTO = sessionDTO)
    }
}