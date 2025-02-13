package domain.usecase.sql_database.local.user

import data.model.regular.user.User
import domain.repository.UserRepository

class InsertNewUserUseCase(private val userRepository: UserRepository) {
    @Throws(Exception::class)
    suspend fun execute(user: User) {
        userRepository.insertNewUser(user = user)
    }
}