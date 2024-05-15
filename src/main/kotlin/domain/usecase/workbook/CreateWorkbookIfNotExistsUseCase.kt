package domain.usecase.workbook

import data.Constants
import domain.repository.WorkbookRepository
import org.apache.poi.xssf.usermodel.XSSFWorkbookType

class CreateWorkbookIfNotExistsUseCase(private val workbookRepository: WorkbookRepository) {
    suspend fun execute(fullPath: String, folderPath: String, dataFormats: Map<String, Boolean>): Boolean {

        val xlsx = XSSFWorkbookType.XLSX
        val xlsm = XSSFWorkbookType.XLSM

        val formats = mutableListOf<XSSFWorkbookType>()

        dataFormats.forEach { (t, u) ->
            if (u && t == Constants.Settings.XLSX) {
                formats.add(xlsx)
            } else if (u && t == Constants.Settings.XLSM) {
                formats.add(xlsm)
            }
        }

        return workbookRepository.createWorkbookIfNotExists(
            fullPath = fullPath,
            folderPath = folderPath,
            formats = formats
        )
    }
}