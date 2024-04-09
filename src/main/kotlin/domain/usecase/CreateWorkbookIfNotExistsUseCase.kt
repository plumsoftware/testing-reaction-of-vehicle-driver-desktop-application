package domain.usecase

import domain.repository.WorkbookRepository

class CreateWorkbookIfNotExistsUseCase(private val workbookRepository: WorkbookRepository) {
    suspend fun execute(fullPath: String, folderPath: String): Boolean =
        workbookRepository.createWorkbookIfNotExists(fullPath = fullPath, folderPath = folderPath)
}