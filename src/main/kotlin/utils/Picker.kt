package utils

import java.io.File
import javax.swing.JFileChooser

fun showFilePicker(fileSelectionMode: Int = JFileChooser.DIRECTORIES_ONLY): File {
    val fileChooser = JFileChooser()
    fileChooser.fileSelectionMode = fileSelectionMode
    val result = fileChooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile
    } else {
        File("C:\\")
    }
}