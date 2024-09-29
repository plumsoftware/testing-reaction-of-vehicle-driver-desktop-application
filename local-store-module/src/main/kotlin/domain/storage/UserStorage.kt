package domain.storage

import data.model.either.local.LocalEither
import data.model.regular.user.User
import domain.usecase.sql_database.local.GetUserByLoginAndPasswordUseCase
import domain.usecase.sql_database.local.user.DeleteUserUseCase
import domain.usecase.sql_database.local.user.UpdateUserUseCase
import domain.usecase.sql_database.local.user.GetAllUsersUseCase
import domain.usecase.sql_database.local.user.GetSessionsWithUserIdUseCase
import domain.usecase.sql_database.local.user.InsertNewUserUseCase
import domain.usecase.sql_database.local.user.IsPasswordUniqueUseCase
import kotlin.Exception

class UserStorage
    (
    private val getUserByLoginAndPasswordUseCase: GetUserByLoginAndPasswordUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getSessionsWithUserIdUseCase: GetSessionsWithUserIdUseCase,
    private val insertNewUserUseCase: InsertNewUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val isPasswordUniqueUseCase: IsPasswordUniqueUseCase
) {
    suspend fun getUserByLoginAndPassword(login: String, password: String): LocalEither<Exception, User> {
        return getUserByLoginAndPasswordUseCase.execute(login = login, password = password)
    }

    suspend fun getAllUsers() = getAllUsersUseCase.execute()
    suspend fun getSessions(userId: Long) = getSessionsWithUserIdUseCase.execute(userId = userId)

    @Throws(Exception::class)
    suspend fun insert(user: User) =
        insertNewUserUseCase.execute(user = user)

    suspend fun isPasswordUnique(password: String): Boolean = isPasswordUniqueUseCase.execute(password = password)

    suspend fun update(user: User) =
        updateUserUseCase.execute(user = user)

    suspend fun delete(userId: Long) = deleteUserUseCase.execute(userId = userId)
}