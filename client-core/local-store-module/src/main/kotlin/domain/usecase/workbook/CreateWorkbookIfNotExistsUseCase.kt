package domain.usecase.workbook

import data.constant.SettingsConstants
import domain.repository.WorkbookRepository
import org.apache.poi.xssf.usermodel.XSSFWorkbookType

class CreateWorkbookIfNotExistsUseCase(private val workbookRepository: WorkbookRepository) {
    suspend fun execute(folderPath: String, dataFormats: Map<String, Boolean>) {

        val formats = mutableListOf<XSSFWorkbookType>()
        val extensions = mutableListOf<String>()

        dataFormats.forEach { (t, u) ->
            if (u) {
                val item = SettingsConstants.FORMAT_LIST[t] ?: XSSFWorkbookType.XLSX
                formats.add(item)
                extensions.add(t)
            }
        }

        workbookRepository.createWorkbookIfNotExists(
            folderPath = folderPath,
            formats = formats,
            extensions = extensions
        )
    }
}