package data.constant

import org.apache.poi.xssf.usermodel.XSSFWorkbookType

object SettingsConstants {
    const val XLSX: String = ".xlsx" //Used
    const val XLSM: String = ".xlsm"
    const val XLTX: String = ".xltx" //Used
    const val XLS: String = ".xls" //Used

    const val NETWORK_DRIVE: String = ""
    const val LOCAL_DRIVE: String = ""
    const val LOCAL_FOLDER_TO_TABLE: String = "C:\\Users\\Default"
    val ADMIN: String = System.getProperty("user.name")

    val FORMAT_LIST = mapOf<String, XSSFWorkbookType>(
        ".xlsx" to XSSFWorkbookType.XLSX,
        ".xls" to XSSFWorkbookType.XLSX,
        ".xltx" to XSSFWorkbookType.XLSX,
    )
}