package domain.repository

import ru.plumsoftware.users.Users

interface UserRepository {
    suspend fun getUserByLoginAndPassword(login: String, password: String) : List<Users>
}