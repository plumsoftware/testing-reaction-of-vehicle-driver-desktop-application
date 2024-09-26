package utlis

import java.io.File

fun createFolderIfNotExists(directoryPath: String) {
    val folder = File(directoryPath)
    if (!folder.exists())
        folder.mkdir()
}