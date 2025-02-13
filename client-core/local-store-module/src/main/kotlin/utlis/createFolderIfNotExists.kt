package utlis

import java.io.File

fun createFolderIfNotExists(directoryPath: String) {

    val parts = directoryPath.split("\\\\")

    var currentPath = ""
    for (part in parts) {
        currentPath += "$part\\\\"
        val directory = File(currentPath)
        if (!directory.exists()) {
            directory.mkdirs()
            println("Создана директория: $currentPath")
        } else {
            println("Директория $currentPath уже существует.")
        }
    }
}