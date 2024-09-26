package domain.repository

import data.model.regular.settings.Settings
import kotlinx.coroutines.CoroutineScope

interface SettingsRepository {
    fun saveData(settings: Settings)

    fun loadSettings(scope: CoroutineScope): Settings
}