package domain.usecase.sql_database.local.user

import data.model.regular.user.User
import domain.repository.UserRepository

class UpdateUserUseCase(private val userRepository: UserRepository) {
    suspend fun execute(user: User) {
        userRepository.updateUser(user = user)
    }
}