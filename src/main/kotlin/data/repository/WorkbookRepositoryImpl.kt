package data.repository

import data.Constants
import domain.repository.WorkbookRepository
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
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
            val calendar: Calendar = Calendar.getInstance()
            val dateFormat: String =
                SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(Date(calendar.timeInMillis))

            val path = "$folderPath\\${Constants.Table.FILE_NAME} ${dateFormat}${extension.lowercase()}"

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
                    var cellItem = row.createCell(i)
                    when (i) {
                        0 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Фамилия")
                            sheet.setColumnWidth(i, 5000)

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
                            cellItem.setCellValue("Имя")
                            sheet.setColumnWidth(i, 5000)

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
                            cellItem.setCellValue("Отчество")
                            sheet.setColumnWidth(i, 5000)

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
                            cellItem.setCellValue("Пол")
                            sheet.setColumnWidth(i, 5000)

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
                            cellItem.setCellValue("Возраст")
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

                        5 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Интервал сигнала в миллисекундах")
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
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_TURQUOISE.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        6 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Режим тестирования")
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
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_TURQUOISE.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        7 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Интервал сигнала (в миллисекундах)")
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
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_TURQUOISE.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        8 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Стандартное отклонение")
                            sheet.setColumnWidth(i, 5000)

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
                            cellStyle.fillForegroundColor = IndexedColors.LIGHT_TURQUOISE.getIndex()
                            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                            cellStyle.setFont(font)
                            cellItem.cellStyle = cellStyle
                        }

                        9 -> {
                            cellItem = row.createCell(i)
                            cellItem.setCellValue("Количество ошибок")
                            sheet.setColumnWidth(i, 5000)

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
                            cellItem.setCellValue("Количество попыток")
                            sheet.setColumnWidth(i, 5000)

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