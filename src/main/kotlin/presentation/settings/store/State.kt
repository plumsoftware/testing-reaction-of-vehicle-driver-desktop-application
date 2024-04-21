package presentation.settings.store

import domain.model.Settings

data class State(
    val isDarkTheme: Boolean = false,
    val isXlsxFormat: Boolean = true,
    val isXlsFormat: Boolean = false,

    val settings: Settings = Settings()
)