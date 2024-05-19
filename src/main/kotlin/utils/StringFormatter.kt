package utils

fun stringFormatter(originalText: String): String {
    var formattedText = originalText.replace("**", "<b>")
    formattedText = formattedText.replace("**", "</b>")
    return formattedText
}