package domain.usecase.workbook

import domain.repository.WorkbookRepository

class CreateWorkbookIfNotExistsUseCase(private val workbookRepository: WorkbookRepository) {
    suspend fun execute(fullPath: String, folderPath: String, dataFormats: Map<String, Boolean>): Boolean =
        workbookRepository.createWorkbookIfNotExists(fullPath = fullPath, folderPath = folderPath, dataFormats = dataFormats)
}