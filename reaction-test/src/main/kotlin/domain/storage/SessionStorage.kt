package domain.storage

import domain.model.dto.database.SessionDTO
import domain.usecase.sql_database.local.GetAllSessionsDtoFromDatabaseUseCase
import domain.usecase.sql_database.local.GetLastSessionIdUseCase
import domain.usecase.sql_database.local.InsertOrAbortNewSessionUseCase
import domain.usecase.sql_database.roaming.InsertOrAbortNewSessionToRoamingDatabaseUseCase

class SessionStorage(
    private val getAllSessionsDtoFromDatabaseUseCase: GetAllSessionsDtoFromDatabaseUseCase,
    private val insertOrAbortNewSessionUseCase: InsertOrAbortNewSessionUseCase,
    private val getLastSessionIdUseCase: GetLastSessionIdUseCase,
    private val insertOrAbortNewSessionToRoamingDatabaseUseCase: InsertOrAbortNewSessionToRoamingDatabaseUseCase
) {
    suspend fun getAllSessionsDto(): List<SessionDTO> {
        return getAllSessionsDtoFromDatabaseUseCase.getAllSessionsDtoFromDatabase()
    }

    suspend fun insertOrAbort(sessionDTO: SessionDTO) {
        insertOrAbortNewSessionUseCase.insertOrAbortNewSession(sessionDTO = sessionDTO)
    }

    suspend fun getLastSessionIdUseCase(): Long {
        return getLastSessionIdUseCase.execute()
    }

    suspend fun insertOrAbortNewSessionToRoamingDatabase(sessionDTO: SessionDTO) {
        insertOrAbortNewSessionToRoamingDatabaseUseCase.execute(sessionDTO = sessionDTO)
    }
}