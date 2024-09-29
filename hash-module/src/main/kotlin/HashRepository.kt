fun interface HashRepository {
    suspend fun hash(text: String) : ByteArray
}