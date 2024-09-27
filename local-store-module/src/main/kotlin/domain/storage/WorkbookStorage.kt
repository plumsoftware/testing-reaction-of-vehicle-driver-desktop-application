package domain.storage

import data.model.dto.TestDTO
import domain.usecase.workbook.CreateWorkbookIfNotExistsUseCase
import domain.usecase.workbook.WriteDataToWorkbookUseCase

class WorkbookStorage(
    private val createWorkbookIfNotExistsUseCase: CreateWorkbookIfNotExistsUseCase,
    private val writeDataToWorkbookUseCase: WriteDataToWorkbookUseCase
) {
    suspend fun createWorkbookIfNotExists(folderPath: String, dataFormats: Map<String, Boolean>) =
        createWorkbookIfNotExistsUseCase.execute(folderPath = folderPath, dataFormats = dataFormats)

    suspend fun writeDataToWorkbook(
        testDTO: TestDTO,
        folderPath: String,
        dataFormats: Map<String, Boolean>,
    ) =
        writeDataToWorkbookUseCase.execute(
            testDTO = testDTO,
            folderPath = folderPath,
            dataFormats = dataFormats
        )
}