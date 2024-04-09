package domain.storage

import domain.usecase.CreateWorkbookIfNotExistsUseCase

class WorkbookStorage(
    private val createWorkbookIfNotExistsUseCase: CreateWorkbookIfNotExistsUseCase
) {
    suspend fun createWorkbookIfNotExistsUseCase(path: String): Boolean =
        createWorkbookIfNotExistsUseCase.execute(path = path)
}