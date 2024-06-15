package domain.storage

import domain.model.regular.settings.Settings
import domain.usecase.settings.GetUserSettingsUseCase
import domain.usecase.settings.SaveUserSettingsUseCase
import kotlinx.coroutines.CoroutineScope

class SettingsStorage(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val saveUserSettingsUseCase: SaveUserSettingsUseCase
) {
    fun save(settings: Settings) {
        saveUserSettingsUseCase.execute(settings)
    }

    fun get(scope: CoroutineScope) = getUserSettingsUseCase.execute(scope = scope)
}