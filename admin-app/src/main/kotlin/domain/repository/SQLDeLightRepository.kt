package domain.repository

import domain.model.dto.database.SessionDTO
import domain.model.regular.user.User
import ru.plumsoftware.sessions.Sessions
import ru.plumsoftware.users.Users

interface SQLDeLightRepository {
    suspend fun getAllUsers(): List<Users>
    suspend fun getAllSessions(): List<SessionDTO>

    suspend fun getSessionsWithUserId(id: Long): List<Sessions>

    suspend fun insertNewUser(user: User, login: String, password: String)

    suspend fun updateUser(user: User, login: String, password: String)

    suspend fun deleteUser(id: Long)
}