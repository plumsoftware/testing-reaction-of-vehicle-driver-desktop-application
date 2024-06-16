package domain.repository

import domain.model.either.LocalEither
import domain.model.regular.user.User

interface UserRepository {
    suspend fun getAllUsers(): List<Users>

    suspend fun getSessionsWithUserId(id: Long): List<Sessions>

    @Throws(Exception::class)
    suspend fun insertNewUser(user: User)

    suspend fun getAllPasswords(): List<String>

    suspend fun updateUser(user: User)

    suspend fun deleteUser(id: Long)

    suspend fun getUserByLoginAndPassword(login: String, password: String): LocalEither<Exception, List<Users>>
}