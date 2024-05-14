package domain.storage

import domain.usecase.workbook.CreateWorkbookIfNotExistsUseCase

class WorkbookStorage(
    private val createWorkbookIfNotExistsUseCase: CreateWorkbookIfNotExistsUseCase
) {
    suspend fun createWorkbookIfNotExistsUseCase(fullPath: String, folderPath: String, dataFormats: Map<String, Boolean>): Boolean =
        createWorkbookIfNotExistsUseCase.execute(fullPath = fullPath, folderPath = folderPath, dataFormats = dataFormats)
}