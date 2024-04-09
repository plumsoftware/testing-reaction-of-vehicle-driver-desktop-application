package domain.repository

interface WorkbookRepository {
    suspend fun createWorkbookIfNotExists(path: String) : Boolean
}