package data.repository

import data.Constants
import domain.model.dto.TestDTO
import domain.repository.WorkbookRepository
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbookType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class WorkbookRepositoryImpl : WorkbookRepository {
    override suspend fun createWorkbookIfNotExists(
        folderPath: String,
        formats: List<XSSFWorkbookType>,
        extensions: MutableList<String>
    ) {
        extensions.forEachIndexed { index, extension ->

            val path = "$folderPath\\${Constants.Table.FILE_NAME}${extension.lowercase()}"

            var workbook: Workbook? = getWorkbook(path = path)

            if (workbook == null) {
//            Folder
                val folder = File(folderPath)
                if (!folder.exists()) {
                    folder.mkdir()
                }

//            Variables
                workbook = XSSFWorkbook(formats[index])

//            Create sheet "Данные тестирования"
                val sheet = workbook.createSheet(Constants.Table.SHEET_NAME)

//            Cell style
                val font = workbook.createFont() as XSSFFont
                font.bold = true

                var cellStyle = workbook.createCellStyle()
                cellStyle.borderBottom = BorderStyle.THIN
                cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                cellStyle.borderTop = BorderStyle.THIN
                cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                cellStyle.borderRight = BorderStyle.THIN
                cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                cellStyle.borderLeft = BorderStyle.THIN
                cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                cellStyle.wrapText = true


//            Create base data
                val row = sheet.createRow(0)

                for (i in 0..10) {
                    var cellItem: XSSFCell
                    when (i) {
                        0 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Униклаьный ID сессии")
                            sheet.setColumnWidth(i, 25000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        1 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Униклаьный ID пользователя")
                            sheet.setColumnWidth(i, 25000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        2 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("ID теста")
                            sheet.setColumnWidth(i, 10000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        3 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Дата")
                            sheet.setColumnWidth(i, 15000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        4 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Категоория вод. удостоверения")
                            sheet.setColumnWidth(i, 25000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        5 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Интервал сигнала в секундах")
                            sheet.setColumnWidth(i, 10000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        6 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Стаж воздения")
                            sheet.setColumnWidth(i, 10000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        7 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Количество ошибок")
                            sheet.setColumnWidth(i, 15000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        8 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Количестыо попыток")
                            sheet.setColumnWidth(i, 15000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_YELLOW.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        9 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Среднее значение в секундах")
                            sheet.setColumnWidth(i, 25000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_GREEN.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        10 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Стандартное отклонение")
                            sheet.setColumnWidth(i, 25000)

//                        Cell style
                            cellStyle = workbook.createCellStyle()
                            cellStyle.borderBottom = BorderStyle.THIN
                            cellStyle.bottomBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderTop = BorderStyle.THIN
                            cellStyle.topBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderRight = BorderStyle.THIN
                            cellStyle.rightBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.borderLeft = BorderStyle.THIN
                            cellStyle.leftBorderColor = IndexedColors.BLACK.getIndex()
                            cellStyle.wrapText = true
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_GREEN.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }
                    }
                }
//            Save
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
        extensions.forEachIndexed { index, extension ->
            val path = "$folderPath\\${Constants.Table.FILE_NAME}${extension.lowercase()}"
            var rowIndex = 0
            var cellIndex = 0

            val workbook: Workbook? = getWorkbook(path = path)

            if (workbook != null) {
                val sheet = workbook.getSheetAt(0)

//                Cell style
                val cellStyle = workbook.createCellStyle()
                cellStyle.wrapText = true


//                Get last indexes
                for (rowItem in sheet) {
                    rowIndex++
                    for (cellItem in rowItem) {
                        cellIndex++
                    }
                }

//                Write user data
                val rowItem = sheet.createRow(rowIndex)

                val calendar: Calendar = Calendar.getInstance()
                val dateFormat: String =
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(calendar.timeInMillis))

                val sessionIdCellItem: Cell = rowItem.createCell(0)
                sessionIdCellItem.setCellValue(testDTO.sessionId.toString())

                val userIdCellItem: Cell = rowItem.createCell(1)
                userIdCellItem.setCellValue(testDTO.user.id.toString())

                val testIdCellItem: Cell = rowItem.createCell(2)
                testIdCellItem.setCellValue(testDTO.testMode?.id.toString())

                val dateCellItem: Cell = rowItem.createCell(3)
                dateCellItem.setCellValue(dateFormat)

                val DLCellItem: Cell = rowItem.createCell(4)
                DLCellItem.setCellValue(testDTO.user.drivingLicenseCategory.toString())

                val signalIntervalCellItem: Cell = rowItem.createCell(5)
                signalIntervalCellItem.setCellValue(testDTO.interval.toString())

                val experienceCellItem: Cell = rowItem.createCell(6)
                experienceCellItem.setCellValue(testDTO.user.experience.toString())

                val errorsCellItem: Cell = rowItem.createCell(7)
                errorsCellItem.setCellValue(testDTO.errorsCount.toString())

                val countCellItem: Cell = rowItem.createCell(8)
                countCellItem.setCellValue(testDTO.count.toString())

                val averageCellItem: Cell = rowItem.createCell(9)
                averageCellItem.setCellValue(testDTO.averageValue.toString())

                val stdDeviationCellItem: Cell = rowItem.createCell(10)
                stdDeviationCellItem.setCellValue(testDTO.stdDeviationValue.toString())

                saveInFile(path, workbook)
            }
        }
        println(testDTO.toString())
    }

    private fun saveInFile(path: String, workbook: Workbook): Boolean {
//        Path to Excel file
        val file = File(path)
        FileOutputStream(file).use { fileOut ->
            workbook.write(fileOut)
            fileOut.flush()
            fileOut.close()
            workbook.close()
            return true
        }
    }

    private fun getWorkbook(path: String): Workbook? {
        try {
            val file = File(path)
            val fileInputStream = FileInputStream(file)
            val workbook: Workbook = XSSFWorkbook(fileInputStream)
            fileInputStream.close()
            return workbook
        } catch (e: Exception) {
            return null
        }
    }
}