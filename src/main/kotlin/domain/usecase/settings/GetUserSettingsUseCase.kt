package domain.usecase.settings

import domain.repository.SettingsRepository

class GetUserSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun execute() = settingsRepository.loadSettings()

}