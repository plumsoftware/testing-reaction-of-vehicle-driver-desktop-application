package domain.storage

import domain.model.regular.Settings
import domain.usecase.settings.GetUserSettingsUseCase
import domain.usecase.settings.SaveUserSettingsUseCase

class SettingsStorage(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val saveUserSettingsUseCase: SaveUserSettingsUseCase
) {
    suspend fun save(settings: Settings) {
        saveUserSettingsUseCase.execute(settings)
    }

    suspend fun get() = getUserSettingsUseCase.execute()
}