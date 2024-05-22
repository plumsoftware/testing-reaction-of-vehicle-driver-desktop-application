package domain.repository

import domain.model.dto.TestDTO
import org.apache.poi.xssf.usermodel.XSSFWorkbookType

interface WorkbookRepository {
    suspend fun createWorkbookIfNotExists(
        folderPath: String,
        formats: List<XSSFWorkbookType>,
        extensions: MutableList<String>
    )

    suspend fun writeDataInWorkbook(
        testDTO: TestDTO,
        folderPath: String,
        formats: List<XSSFWorkbookType>,
        extensions: MutableList<String>,
    )
}