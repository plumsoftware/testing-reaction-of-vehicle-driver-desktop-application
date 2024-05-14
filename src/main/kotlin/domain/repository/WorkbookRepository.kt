package domain.repository

interface WorkbookRepository {
    suspend fun createWorkbookIfNotExists(fullPath: String, folderPath: String, dataFormats: Map<String,Boolean>) : Boolean
}