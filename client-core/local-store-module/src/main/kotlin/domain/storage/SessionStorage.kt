package domain.storage

import data.model.dto.database.SessionDTO
import domain.usecase.sql_database.local.GetAllSessionsDtoFromDatabaseUseCase
import domain.usecase.sql_database.local.GetLastSessionIdUseCase
import domain.usecase.sql_database.local.InsertOrAbortNewSessionUseCase
import domain.usecase.sql_database.local.user.GetSessionsWithUserIdUseCase

class SessionStorage(
    private val getAllSessionsDtoFromDatabaseUseCase: GetAllSessionsDtoFromDatabaseUseCase,
    private val insertOrAbortNewSessionUseCase: InsertOrAbortNewSessionUseCase,
    private val getSessionsWithUserIdUseCase: GetSessionsWithUserIdUseCase,
    private val getLastSessionIdUseCase: GetLastSessionIdUseCase,
) {
    suspend fun getAllSessionsDto(): List<SessionDTO> {
        return getAllSessionsDtoFromDatabaseUseCase.getAllSessionsDtoFromDatabase()
    }

    /** Сохраняет сессию и возвращает её session_id. */
    suspend fun insertOrAbort(sessionDTO: SessionDTO): Long {
        return insertOrAbortNewSessionUseCase.insertOrAbortNewSession(sessionDTO = sessionDTO)
    }

    suspend fun getSessionsWithUserId(userId: Long) = getSessionsWithUserIdUseCase.execute(userId = userId)

    suspend fun getLastSessionIdUseCase(): Long {
        return getLastSessionIdUseCase.execute()
    }
}