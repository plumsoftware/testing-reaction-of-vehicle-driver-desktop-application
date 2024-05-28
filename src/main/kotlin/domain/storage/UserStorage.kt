package domain.storage

import domain.model.either.RoamingEither
import domain.model.regular.user.User
import domain.usecase.sql_database.roaming.GetUserByLoginAndPasswordUseCase
import kotlin.Exception

class UserStorage(private val getUserByLoginAndPasswordUseCase: GetUserByLoginAndPasswordUseCase) {
    suspend fun getUserByLoginAndPassword(login: String, password: String): RoamingEither<Exception, User> {
        return getUserByLoginAndPasswordUseCase.execute(login = login, password = password)
    }
}