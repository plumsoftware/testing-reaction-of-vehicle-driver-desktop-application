package utlis

import java.io.File

fun createFolderIfNotExists(directoryPath: String) {
    val directory = File(directoryPath)
    if (!directory.exists() && directory.mkdirs()) {
        println("Создана директория: ${directory.absolutePath}")
    }
}
