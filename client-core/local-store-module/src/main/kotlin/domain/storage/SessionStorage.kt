package domain.storage

import data.model.dto.database.SessionDTO
import data.model.dto.test.TrialRecord
import domain.usecase.sql_database.local.GetAllSessionsDtoFromDatabaseUseCase
import domain.usecase.sql_database.local.GetLastSessionIdUseCase
import domain.usecase.sql_database.local.GetSessionTrialsUseCase
import domain.usecase.sql_database.local.InsertOrAbortNewSessionUseCase
import domain.usecase.sql_database.local.user.GetSessionsWithUserIdUseCase

class SessionStorage(
    private val getAllSessionsDtoFromDatabaseUseCase: GetAllSessionsDtoFromDatabaseUseCase,
    private val insertOrAbortNewSessionUseCase: InsertOrAbortNewSessionUseCase,
    private val getSessionsWithUserIdUseCase: GetSessionsWithUserIdUseCase,
    private val getLastSessionIdUseCase: GetLastSessionIdUseCase,
    private val getSessionTrialsUseCase: GetSessionTrialsUseCase,
) {
    suspend fun getAllSessionsDto(): List<SessionDTO> {
        return getAllSessionsDtoFromDatabaseUseCase.getAllSessionsDtoFromDatabase()
    }

    /** Сохраняет сессию вместе с попытками и возвращает её session_id. */
    suspend fun insertOrAbort(sessionDTO: SessionDTO, trials: List<TrialRecord> = emptyList()): Long {
        return insertOrAbortNewSessionUseCase.insertOrAbortNewSession(sessionDTO = sessionDTO, trials = trials)
    }

    suspend fun getSessionsWithUserId(userId: Long) = getSessionsWithUserIdUseCase.execute(userId = userId)

    /** Попытки сессии по порядку (пусто для сессий, записанных до версии методики 2). */
    suspend fun getTrials(sessionId: Long): List<TrialRecord> = getSessionTrialsUseCase.execute(sessionId = sessionId)

    suspend fun getLastSessionIdUseCase(): Long {
        return getLastSessionIdUseCase.execute()
    }
}