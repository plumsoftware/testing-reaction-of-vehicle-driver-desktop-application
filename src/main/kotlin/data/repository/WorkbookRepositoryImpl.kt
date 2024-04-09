package data.repository

import domain.repository.WorkbookRepository

class WorkbookRepositoryImpl : WorkbookRepository {
    override suspend fun createWorkbookIfNotExists(path: String) : Boolean {
        TODO("Not yet implemented")
    }
}