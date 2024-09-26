package data

import domain.CryptographyRepository

class CryptographyRepositoryImpl : CryptographyRepository {
    override suspend fun encode(text: String, key: String): String {
        val sb = StringBuilder()
        for (i in text.indices) {
            val c = text[i]
            val k = key[i % key.length]
            val encoded = c.code xor k.code
            sb.append(encoded.toChar())
        }

        println(sb.toString())
        return sb.toString()
    }

    override suspend fun decode(text: String, key: String): String {
        val sb = StringBuilder()
        for (i in text.indices) {
            val c = text[i]
            val k = key[i % key.length]
            val decoded = c.code xor k.code
            sb.append(decoded.toChar())
        }
        println(sb.toString())
        return sb.toString()
    }

}