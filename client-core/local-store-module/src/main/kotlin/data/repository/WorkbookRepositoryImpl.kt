package data.repository

import data.constant.ExcelConstants
import data.model.dto.test.TestDTO
import domain.repository.WorkbookRepository
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbookType
import utlis.parseToExcelDateTime
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class WorkbookRepositoryImpl : WorkbookRepository {

    private data class Column(val title: String, val width: Int, val color: IndexedColors)

    /**
     * Порядок столбцов 0–10 совпадает со старыми файлами; новые столбцы добавлены в конец,
     * поэтому существующие таблицы продолжают заполняться корректно.
     */
    private val columns = listOf(
        Column("Уникальный ID сессии", 25000, IndexedColors.LIGHT_YELLOW),
        Column("Уникальный ID пользователя", 25000, IndexedColors.LIGHT_YELLOW),
        Column("ID теста", 10000, IndexedColors.LIGHT_YELLOW),
        Column("Дата", 15000, IndexedColors.LIGHT_YELLOW),
        Column("Категория вод. удостоверения", 25000, IndexedColors.LIGHT_YELLOW),
        Column("Интервал сигнала в секундах", 10000, IndexedColors.LIGHT_YELLOW),
        Column("Стаж вождения", 10000, IndexedColors.LIGHT_YELLOW),
        Column("Количество ошибок (всего)", 15000, IndexedColors.LIGHT_YELLOW),
        Column("Количество попыток", 15000, IndexedColors.LIGHT_YELLOW),
        Column("Среднее время правильных реакций, с", 25000, IndexedColors.LIGHT_GREEN),
        Column("Стандартное отклонение (выборочное), с", 25000, IndexedColors.LIGHT_GREEN),
        Column("Возраст", 10000, IndexedColors.LIGHT_YELLOW),
        Column("Учтено правильных реакций (n)", 15000, IndexedColors.LIGHT_GREEN),
        Column("Ошибки: неверная реакция", 15000, IndexedColors.LIGHT_YELLOW),
        Column("Ошибки: пропуск сигнала", 15000, IndexedColors.LIGHT_YELLOW),
        Column("Ошибки: фальстарт", 15000, IndexedColors.LIGHT_YELLOW),
        Column("Время правильных реакций по попыткам, мс", 40000, IndexedColors.LIGHT_GREEN),
        Column("Ведущая рука", 10000, IndexedColors.LIGHT_YELLOW),
        Column("Номер сессии пользователя в этом тесте", 15000, IndexedColors.LIGHT_YELLOW),
        Column("Коррекция зрения", 12000, IndexedColors.LIGHT_TURQUOISE),
        Column("Сон за сутки, ч", 10000, IndexedColors.LIGHT_TURQUOISE),
        Column("Самочувствие (1–5)", 10000, IndexedColors.LIGHT_TURQUOISE),
        Column("Рука ответа", 10000, IndexedColors.LIGHT_TURQUOISE),
        Column("Устройство ввода", 10000, IndexedColors.LIGHT_TURQUOISE),
        Column("Монитор", 20000, IndexedColors.GREY_25_PERCENT),
        Column("Размер изображения монитора, см", 12000, IndexedColors.GREY_25_PERCENT),
        Column("Диагональ монитора, дюймы", 10000, IndexedColors.GREY_25_PERCENT),
        Column("Разрешение экрана, px", 10000, IndexedColors.GREY_25_PERCENT),
        Column("Частота обновления, Гц", 10000, IndexedColors.GREY_25_PERCENT),
        Column("Масштаб ОС, %", 8000, IndexedColors.GREY_25_PERCENT),
        Column("Размер окна теста, px", 10000, IndexedColors.GREY_25_PERCENT),
        Column("Компьютер", 12000, IndexedColors.GREY_25_PERCENT),
        Column("ОС", 12000, IndexedColors.GREY_25_PERCENT),
        Column("Версия программы", 8000, IndexedColors.GREY_25_PERCENT),
        Column("Версия методики", 8000, IndexedColors.GREY_25_PERCENT),
    )

    /** Лист с каждой попыткой: одна строка на сигнал или фальстарт. */
    private val trialColumns = listOf(
        Column("Уникальный ID сессии", 8000, IndexedColors.LIGHT_YELLOW),
        Column("Уникальный ID пользователя", 8000, IndexedColors.LIGHT_YELLOW),
        Column("ID теста", 5000, IndexedColors.LIGHT_YELLOW),
        Column("Дата", 10000, IndexedColors.LIGHT_YELLOW),
        Column("Номер попытки", 6000, IndexedColors.LIGHT_YELLOW),
        Column("Сигнал", 8000, IndexedColors.LIGHT_YELLOW),
        Column("Интервал до сигнала, мс", 8000, IndexedColors.LIGHT_YELLOW),
        Column("Исход", 10000, IndexedColors.LIGHT_GREEN),
        Column("Время реакции, мс (отрицательное — нажатие до сигнала)", 12000, IndexedColors.LIGHT_GREEN),
    )

    override suspend fun createWorkbookIfNotExists(
        folderPath: String,
        formats: List<XSSFWorkbookType>,
        extensions: MutableList<String>
    ) {
        extensions.forEachIndexed { index, extension ->

            val path = "$folderPath\\${ExcelConstants.FILE_NAME}${extension.lowercase()}"

            if (getWorkbook(path = path) == null) {
                val folder = File(folderPath)
                if (!folder.exists()) {
                    folder.mkdirs()
                }

                val workbook = XSSFWorkbook(formats[index])
                val sheet = workbook.createSheet(ExcelConstants.SHEET_NAME)
                writeHeader(workbook = workbook, sheet = sheet, columns = columns)
                val trialsSheet = workbook.createSheet(ExcelConstants.TRIALS_SHEET_NAME)
                writeHeader(workbook = workbook, sheet = trialsSheet, columns = trialColumns)
                saveInFile(path, workbook)
            }
        }
    }

    override suspend fun writeDataInWorkbook(
        testDTO: TestDTO,
        folderPath: String,
        formats: List<XSSFWorkbookType>,
        extensions: MutableList<String>,
    ) {
        extensions.forEach { extension ->
            val path = "$folderPath\\${ExcelConstants.FILE_NAME}${extension.lowercase()}"

            val workbook: Workbook = getWorkbook(path = path) ?: return@forEach
            val sheet = workbook.getSheetAt(0)

            // В файлах, созданных старой версией, дописываем заголовки новых столбцов и лист попыток
            writeHeader(workbook = workbook, sheet = sheet, columns = columns)
            val trialsSheet = workbook.getSheet(ExcelConstants.TRIALS_SHEET_NAME)
                ?: workbook.createSheet(ExcelConstants.TRIALS_SHEET_NAME)
            writeHeader(workbook = workbook, sheet = trialsSheet, columns = trialColumns)

            val rowItem = sheet.createRow(sheet.lastRowNum + 1)
            val dateFormat: String = parseToExcelDateTime(Calendar.getInstance().timeInMillis)

            with(testDTO) {
                rowItem.createCell(0).setCellValue(sessionId.toDouble())
                rowItem.createCell(1).setCellValue(user.id.toDouble())
                testMode?.id?.let { rowItem.createCell(2).setCellValue(it.toDouble()) }
                rowItem.createCell(3).setCellValue(dateFormat)
                rowItem.createCell(4).setCellValue(user.drivingLicenseCategory.toString())
                rowItem.createCell(5).setCellValue(interval.toString())
                rowItem.createCell(6).setCellValue(user.experience.toDouble())
                rowItem.createCell(7).setCellValue((errorsCount ?: 0).toDouble())
                rowItem.createCell(8).setCellValue(count.toDouble())
                rowItem.createCell(9).setCellValue(averageValue)
                rowItem.createCell(10).setCellValue(stdDeviationValue)
                rowItem.createCell(11).setCellValue(user.age.toDouble())
                rowItem.createCell(12).setCellValue(reactionTimesMs.size.toDouble())
                rowItem.createCell(13).setCellValue(wrongResponses.toDouble())
                rowItem.createCell(14).setCellValue(omissions.toDouble())
                rowItem.createCell(15).setCellValue(prematureResponses.toDouble())
                rowItem.createCell(16).setCellValue(
                    reactionTimesMs.joinToString(separator = "; ") { String.format(Locale.ROOT, "%.1f", it) }
                )
                rowItem.createCell(17).setCellValue(user.handedness.toString())
                rowItem.createCell(18).setCellValue(userSessionNumber.toDouble())

                rowItem.createCell(19).setCellValue(preTestState.visionCorrection?.toString() ?: "")
                preTestState.sleepHours?.let { rowItem.createCell(20).setCellValue(it.toDouble()) }
                preTestState.wellBeing?.let { rowItem.createCell(21).setCellValue(it.toDouble()) }
                rowItem.createCell(22).setCellValue(preTestState.responseHand?.toString() ?: "")
                rowItem.createCell(23).setCellValue(preTestState.inputDevice?.toString() ?: "")

                rowItem.createCell(24).setCellValue(environment.monitorName)
                rowItem.createCell(25).setCellValue(environment.monitorSizeCm)
                environment.monitorDiagonalInches?.let { rowItem.createCell(26).setCellValue(it) }
                rowItem.createCell(27).setCellValue(environment.screenResolution)
                environment.refreshRateHz?.let { rowItem.createCell(28).setCellValue(it.toDouble()) }
                environment.scalePercent?.let { rowItem.createCell(29).setCellValue(it.toDouble()) }
                rowItem.createCell(30).setCellValue(environment.testWindowPx)
                rowItem.createCell(31).setCellValue(environment.computerName)
                rowItem.createCell(32).setCellValue(environment.operatingSystem)
                rowItem.createCell(33).setCellValue(environment.appVersion)
                rowItem.createCell(34).setCellValue(environment.methodVersion)

                trials.forEach { trial ->
                    val trialRow = trialsSheet.createRow(trialsSheet.lastRowNum + 1)
                    trialRow.createCell(0).setCellValue(sessionId.toDouble())
                    trialRow.createCell(1).setCellValue(user.id.toDouble())
                    testMode?.id?.let { trialRow.createCell(2).setCellValue(it.toDouble()) }
                    trialRow.createCell(3).setCellValue(dateFormat)
                    trialRow.createCell(4).setCellValue(trial.number.toDouble())
                    trialRow.createCell(5).setCellValue(trial.stimulus ?: "—")
                    trialRow.createCell(6).setCellValue(trial.foreperiodMs.toDouble())
                    trialRow.createCell(7).setCellValue(trial.outcome.title)
                    trial.reactionTimeMs?.let { trialRow.createCell(8).setCellValue(it) }
                }
            }

            saveInFile(path, workbook)
        }
        println(testDTO.toString())
    }

    /** Создаёт недостающие ячейки заголовка в первой строке листа. */
    private fun writeHeader(workbook: Workbook, sheet: Sheet, columns: List<Column>) {
        // Шрифт и стили создаются только для недостающих ячеек: число шрифтов в файле Excel ограничено
        val font by lazy { workbook.createFont().apply { bold = true } }

        val row = sheet.getRow(0) ?: sheet.createRow(0)
        columns.forEachIndexed { index, column ->
            if (row.getCell(index) != null) return@forEachIndexed

            val cellStyle = workbook.createCellStyle()
            cellStyle.borderBottom = BorderStyle.THIN
            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
            cellStyle.borderTop = BorderStyle.THIN
            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
            cellStyle.borderRight = BorderStyle.THIN
            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
            cellStyle.borderLeft = BorderStyle.THIN
            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
            cellStyle.wrapText = true
            cellStyle.fillForegroundColor = column.color.getIndex()
            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
            cellStyle.setFont(font)

            val cell = row.createCell(index)
            cell.setCellValue(column.title)
            cell.cellStyle = cellStyle
            sheet.setColumnWidth(index, column.width)
        }
    }

    private fun saveInFile(path: String, workbook: Workbook): Boolean {
        val file = File(path)
        FileOutputStream(file).use { fileOut ->
            workbook.write(fileOut)
            fileOut.flush()
            workbook.close()
            return true
        }
    }

    private fun getWorkbook(path: String): Workbook? {
        return try {
            FileInputStream(File(path)).use { XSSFWorkbook(it) }
        } catch (e: Exception) {
            null
        }
    }
}
