package domain.storage

import domain.usecase.workbook.CreateWorkbookIfNotExistsUseCase

class WorkbookStorage(
    private val createWorkbookIfNotExistsUseCase: CreateWorkbookIfNotExistsUseCase
) {
    suspend fun createWorkbookIfNotExistsUseCase(folderPath: String, dataFormats: Map<String, Boolean>) =
        createWorkbookIfNotExistsUseCase.execute(folderPath = folderPath, dataFormats = dataFormats)
}