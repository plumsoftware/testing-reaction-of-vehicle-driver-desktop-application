fun interface HashRepository {
    suspend fun hash(text: String) : String
}