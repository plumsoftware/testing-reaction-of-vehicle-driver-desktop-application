package domain.repository

import org.apache.poi.xssf.usermodel.XSSFWorkbookType

interface WorkbookRepository {
    suspend fun createWorkbookIfNotExists(
        fullPath: String,
        folderPath: String,
        formats: List<XSSFWorkbookType>
    ): Boolean
}