package domain.repository

import domain.model.dto.SessionDTO

interface SessionRepository {
    suspend fun getAllSessionFromDatabase() : SessionDTO
}