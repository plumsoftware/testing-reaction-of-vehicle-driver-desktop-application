package data.repository

import data.Constants
import domain.repository.WorkbookRepository
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class WorkbookRepositoryImpl : WorkbookRepository {
    override suspend fun createWorkbookIfNotExists(fullPath: String, folderPath: String): Boolean {
        var workbook: Workbook? = getWorkbook(path = fullPath)

        return if (workbook == null) {
//            Folder
            val folder: File = File(folderPath)
            if (!folder.exists()) {
                folder.mkdir()
            }

//            Variables
            workbook = XSSFWorkbook()

//            Create sheet "Данные тестирования"
            val sheet = workbook.createSheet(Constants.SHEET_NAME)

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

            for (i in 0..9) {
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
                        cellItem.setCellValue("Группа")
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
                        cellItem.setCellValue("Режим")
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
                        cellItem.setCellValue("Среднее значение (миллисекунды)")
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
                        cellStyle.fillForegroundColor = IndexedColors.LIGHT_GREEN.getIndex()
                        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                        cellStyle.setFont(font)
                        cellItem.cellStyle = cellStyle
                    }

                    7 -> {
                        cellItem = row.createCell(i)
                        cellItem.setCellValue("Стандартное отклонение")
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
                        cellStyle.fillForegroundColor = IndexedColors.LIGHT_GREEN.getIndex()
                        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
                        cellStyle.setFont(font)
                        cellItem.cellStyle = cellStyle
                    }

                    8 -> {
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

                    9 -> {
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
            return saveInFile(fullPath, workbook)
        } else {
            true
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