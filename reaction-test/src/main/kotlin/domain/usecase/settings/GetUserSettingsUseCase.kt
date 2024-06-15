package domain.usecase.settings

import domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope

class GetUserSettingsUseCase(private val settingsRepository: SettingsRepository) {
    fun execute(scope: CoroutineScope) = settingsRepository.loadSettings(scope = scope)

}