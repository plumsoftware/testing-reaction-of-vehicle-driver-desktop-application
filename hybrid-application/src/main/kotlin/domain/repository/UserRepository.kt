package domain.repository

import domain.model.regular.user.User
import ru.plumsoftware.sessions.Sessions
import ru.plumsoftware.users.Users

interface UserRepository {
    suspend fun getAllUsers(): List<Users>

    suspend fun getSessionsWithUserId(id: Long): List<Sessions>

    @Throws(Exception::class)
    suspend fun insertNewUser(user: User)

    suspend fun getAllPasswords() : List<String>

    suspend fun updateUser(user: User)

    suspend fun deleteUser(id: Long)
}