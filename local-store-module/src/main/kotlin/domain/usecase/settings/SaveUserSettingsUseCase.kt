package domain.usecase.settings

import data.model.regular.settings.Settings
import domain.repository.SettingsRepository

class SaveUserSettingsUseCase(private val settingsRepository: SettingsRepository) {
    fun execute(settings: Settings) {
        settingsRepository.saveData(settings)
    }
}