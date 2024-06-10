package domain.usecase.sqldelight

import domain.model.regular.user.User
import domain.repository.SQLDeLightRepository

class InsertNewUserUseCase(private val sqlDeLightRepository: SQLDeLightRepository) {
    @Throws(Exception::class)
    suspend fun execute(user: User) {
        sqlDeLightRepository.insertNewUser(user = user)
    }
}