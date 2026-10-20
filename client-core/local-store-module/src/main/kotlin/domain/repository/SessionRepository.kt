package domain.repository

import data.model.dto.database.SessionDTO
import data.model.dto.test.TrialRecord

interface SessionRepository {
    suspend fun getAllSessionDtoFromDatabase() : List<SessionDTO>
    /** Сохраняет сессию вместе с попытками и возвращает её session_id. */
    suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO, trials: List<TrialRecord>): Long
    suspend fun getTrials(sessionId: Long): List<TrialRecord>
    suspend fun getSessionsWithUserId(id: Long): List<SessionDTO>
    suspend fun getLastSessionId() : Long
}