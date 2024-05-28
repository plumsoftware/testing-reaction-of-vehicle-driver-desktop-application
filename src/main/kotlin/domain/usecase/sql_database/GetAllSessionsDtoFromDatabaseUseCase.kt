package domain.usecase.sql_database

import domain.model.dto.database.SessionDTO
import domain.repository.SessionRepository

class GetAllSessionsDtoFromDatabaseUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend fun getAllSessionsDtoFromDatabase(): List<SessionDTO> {
        return sessionRepository.getAllSessionDtoFromDatabase()
    }
}