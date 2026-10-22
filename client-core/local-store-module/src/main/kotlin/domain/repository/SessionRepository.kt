package domain.repository

import data.model.dto.database.SessionDTO
import data.model.dto.test.TestMetric
import data.model.dto.test.TrialRecord

interface SessionRepository {
    suspend fun getAllSessionDtoFromDatabase() : List<SessionDTO>
    /** Сохраняет сессию вместе с попытками и показателями и возвращает её session_id. */
    suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO, trials: List<TrialRecord>, metrics: List<TestMetric>): Long
    suspend fun getTrials(sessionId: Long): List<TrialRecord>
    suspend fun getMetrics(sessionId: Long): List<TestMetric>
    suspend fun getSessionsWithUserId(id: Long): List<SessionDTO>
    suspend fun getLastSessionId() : Long
}