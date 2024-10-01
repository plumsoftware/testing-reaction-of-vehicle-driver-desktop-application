package domain.repository

import data.model.dto.database.SessionDTO

interface SessionRepository {
    suspend fun getAllSessionDtoFromDatabase() : List<SessionDTO>
    suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO)
    suspend fun getSessionsWithUserId(id: Long): List<SessionDTO>
    suspend fun getLastSessionId() : Long
}