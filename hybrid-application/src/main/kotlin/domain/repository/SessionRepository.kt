package domain.repository

import domain.model.dto.database.SessionDTO

interface SessionRepository {
    suspend fun getAllSessionDtoFromDatabase() : List<SessionDTO>
    suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO)
    suspend fun getLastSessionId() : Long
}