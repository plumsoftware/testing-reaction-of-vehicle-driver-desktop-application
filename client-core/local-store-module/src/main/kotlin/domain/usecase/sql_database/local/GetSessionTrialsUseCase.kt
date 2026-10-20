package domain.usecase.sql_database.local

import data.model.dto.test.TrialRecord
import domain.repository.SessionRepository

class GetSessionTrialsUseCase(private val sessionRepository: SessionRepository) {
    suspend fun execute(sessionId: Long): List<TrialRecord> {
        return sessionRepository.getTrials(sessionId = sessionId)
    }
}
