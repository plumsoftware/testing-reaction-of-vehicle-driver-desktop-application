package domain.repository

import org.apache.poi.xssf.usermodel.XSSFWorkbookType

interface WorkbookRepository {
    suspend fun createWorkbookIfNotExists(
        folderPath: String,
        formats: List<XSSFWorkbookType>
    ): Boolean
}