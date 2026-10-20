package domain.repository

import data.model.dto.database.SessionDTO

interface SessionRepository {
    suspend fun getAllSessionDtoFromDatabase() : List<SessionDTO>
    /** Сохраняет сессию и возвращает её session_id. */
    suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO): Long
    suspend fun getSessionsWithUserId(id: Long): List<SessionDTO>
    suspend fun getLastSessionId() : Long
}