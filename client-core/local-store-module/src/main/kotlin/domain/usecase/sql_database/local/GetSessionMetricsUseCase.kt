package domain.usecase.sql_database.local

import data.model.dto.test.TestMetric
import domain.repository.SessionRepository

class GetSessionMetricsUseCase(private val sessionRepository: SessionRepository) {
    suspend fun execute(sessionId: Long): List<TestMetric> {
        return sessionRepository.getMetrics(sessionId = sessionId)
    }
}
