package domain.repository

import domain.model.either.RoamingEither
import ru.plumsoftware.users.Users

interface UserRepository {
    suspend fun getUserByLoginAndPassword(login: String, password: String) : RoamingEither<Exception, List<Users>>
}