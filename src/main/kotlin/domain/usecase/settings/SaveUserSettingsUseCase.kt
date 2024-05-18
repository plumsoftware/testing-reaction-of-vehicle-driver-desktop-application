package domain.usecase.settings

import domain.model.regular.Settings
import domain.repository.SettingsRepository

class SaveUserSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun execute(settings: Settings) {
        settingsRepository.saveData(settings)
    }
}