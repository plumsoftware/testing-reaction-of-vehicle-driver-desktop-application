package domain.storage

import domain.model.regular.user.User
import domain.usecase.sql_database.roaming.GetUserByLoginAndPasswordUseCase

class UserStorage(private val getUserByLoginAndPasswordUseCase: GetUserByLoginAndPasswordUseCase) {
    suspend fun getUserByLoginAndPassword(login: String, password: String): User {
        return getUserByLoginAndPasswordUseCase.execute(login = login, password = password)
    }
}