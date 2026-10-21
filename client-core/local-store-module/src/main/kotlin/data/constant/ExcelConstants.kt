package data.constant

import java.io.File

object ExcelConstants {
    const val SHEET_NAME: String = "Данные тестирования"
    const val TRIALS_SHEET_NAME: String = "Попытки"
    const val METRICS_SHEET_NAME: String = "Показатели"
    const val FILE_NAME: String = "База тестирования"

    /** Файл таблицы результатов в папке [folderPath] для расширения вида «.xlsx». */
    fun workbookFile(folderPath: String, extension: String): File =
        File(folderPath, FILE_NAME + extension.lowercase())
}