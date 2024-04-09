package domain.usecase

import domain.repository.WorkbookRepository

class CreateWorkbookIfNotExistsUseCase(private val workbookRepository: WorkbookRepository) {
    suspend fun execute(path: String): Boolean = workbookRepository.createWorkbookIfNotExists(path = path)
}