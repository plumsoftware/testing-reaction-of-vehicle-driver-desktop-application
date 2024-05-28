package domain.storage

import domain.model.dto.database.SessionDTO
import domain.usecase.sql_database.GetAllSessionsDtoFromDatabaseUseCase
import domain.usecase.sql_database.InsertOrAbortNewSessionUseCase

class SessionStorage(
    private val getAllSessionsDtoFromDatabaseUseCase: GetAllSessionsDtoFromDatabaseUseCase,
    private val insertOrAbortNewSessionUseCase: InsertOrAbortNewSessionUseCase
) {
    suspend fun getAllSessionsDto(): List<SessionDTO> {
        return getAllSessionsDtoFromDatabaseUseCase.getAllSessionsDtoFromDatabase()
    }

    suspend fun insertOrAbort(sessionDTO: SessionDTO) {
        insertOrAbortNewSessionUseCase.insertOrAbortNewSession(sessionDTO = sessionDTO)
    }
}