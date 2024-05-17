package domain.usecase.workbook

import data.Constants
import domain.repository.WorkbookRepository
import org.apache.poi.xssf.usermodel.XSSFWorkbookType

class CreateWorkbookIfNotExistsUseCase(private val workbookRepository: WorkbookRepository) {
    suspend fun execute(folderPath: String, dataFormats: Map<String, Boolean>): Boolean {

        val formats = mutableListOf<XSSFWorkbookType>()

        dataFormats.forEach { (t, u) ->
            if (u) {
                val item = Constants.Settings.FORMAT_LIST[t] ?: XSSFWorkbookType.XLSX
                formats.add(item)
            }
        }

        return workbookRepository.createWorkbookIfNotExists(
            folderPath = folderPath,
            formats = formats
        )
    }
}