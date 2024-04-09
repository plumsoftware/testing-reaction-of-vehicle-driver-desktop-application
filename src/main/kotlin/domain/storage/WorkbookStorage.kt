package domain.storage

import domain.usecase.CreateWorkbookIfNotExistsUseCase

class WorkbookStorage(
    private val createWorkbookIfNotExistsUseCase: CreateWorkbookIfNotExistsUseCase
) {
    suspend fun createWorkbookIfNotExistsUseCase(fullPath: String, folderPath: String): Boolean =
        createWorkbookIfNotExistsUseCase.execute(fullPath = fullPath, folderPath = folderPath)
}