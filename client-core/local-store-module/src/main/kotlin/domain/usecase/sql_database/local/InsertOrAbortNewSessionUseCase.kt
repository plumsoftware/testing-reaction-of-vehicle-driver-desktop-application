package domain.usecase.sql_database.local

import data.model.dto.database.SessionDTO
import data.model.dto.test.TrialRecord
import domain.repository.SessionRepository

class InsertOrAbortNewSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO, trials: List<TrialRecord>): Long {
        return sessionRepository.insertOrAbortNewSession(sessionDTO = sessionDTO, trials = trials)
    }
}