package domain.usecase.sql_database.local.user

import data.model.dto.database.SessionDTO
import domain.repository.SessionRepository

class GetSessionsWithUserIdUseCase(private val sessionRepository: SessionRepository) {
    suspend fun execute(userId: Long): List<SessionDTO> {
        val sessionsWithUserId = sessionRepository.getSessionsWithUserId(id = userId).reversed()
        return sessionsWithUserId
    }
}