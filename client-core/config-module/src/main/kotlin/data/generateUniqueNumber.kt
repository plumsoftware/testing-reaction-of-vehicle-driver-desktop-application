package data

import java.util.Random

inline fun generateUniqueNumber(length: Int = 64) : String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
    val random = Random()
    return (1..length).map { chars[random.nextInt(chars.size)] }.joinToString("")
}