package domain.repository

import domain.model.regular.settings.Settings

interface SettingsRepository {
    suspend fun saveData(settings: Settings)

    suspend fun loadSettings(): Settings
}