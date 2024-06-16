package utils

import java.io.File

fun createFolderIfNotExists(folderPath: String) {
    val folder = File(folderPath)
    if (!folder.exists())
        folder.mkdir()
}