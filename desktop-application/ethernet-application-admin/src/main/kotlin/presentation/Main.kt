package presentation

import AboutUserPage
import about_app.AboutAppPage
import allusers.UsersPage
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import java.io.File
import other.components.StorageErrorDialog
import data.MissingCryptographyKeyException
import data.CryptographyKeyStore
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.model.regular.Mode
import data.repository.SessionRepositoryImpl_
import data.repository.SettingsRepositoryImpl
import data.repository.UserRepositoryImpl
import domain.repository.SessionRepository
import domain.repository.UserRepository
import domain.storage.SessionStorage
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import presentation.home.HomePage
import presentation.home.store.Output
import presentation.home.viewmodel.HomeViewModel
import presentation.settings.SettingsPage

import domain.storage.SettingsStorage
import domain.storage.UserStorage
import domain.usecase.settings.*
import domain.usecase.sql_database.local.GetAllSessionsDtoFromDatabaseUseCase
import domain.usecase.sql_database.local.GetLastSessionIdUseCase
import domain.usecase.sql_database.local.GetSessionMetricsUseCase
import domain.usecase.sql_database.local.GetSessionTrialsUseCase
import domain.usecase.sql_database.local.GetUserByLoginAndPasswordUseCase
import domain.usecase.sql_database.local.InsertOrAbortNewSessionUseCase
import domain.usecase.sql_database.local.user.DeleteUserUseCase
import domain.usecase.sql_database.local.user.GetAllUsersUseCase
import domain.usecase.sql_database.local.user.GetSessionsWithUserIdUseCase
import domain.usecase.sql_database.local.user.InsertNewUserUseCase
import domain.usecase.sql_database.local.user.IsPasswordUniqueUseCase
import domain.usecase.sql_database.local.user.UpdateUserUseCase
import newuser.NewUserPage
import other.extension.route.DesktopRouting
import presentation.main.MainViewModel
import presentation.main.store.Event
import theme.AppTheme

private lateinit var userRepository: UserRepository
private lateinit var userStorage: UserStorage
private lateinit var sessionRepository: SessionRepository
private lateinit var sessionStorage: SessionStorage

fun main() = run {

    println("App started!")

    application {

        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

//        region::Storage
        val settingsRepository = SettingsRepositoryImpl()
        val settingsStorage = SettingsStorage(
            getUserSettingsUseCase = GetUserSettingsUseCase(settingsRepository),
            saveUserSettingsUseCase = SaveUserSettingsUseCase(settingsRepository)
        )

//        endregion

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("main_icon.png", ::loadImageBitmap)),
            title = "Администратор. Тест на реакцию",
            state = windowState,
        ) {
            PreComposeApp {
                val navigator = rememberNavigator()

                val mainViewModel: MainViewModel =
                    viewModel(modelClass = MainViewModel::class) {
                        MainViewModel(settingsStorage = settingsStorage)
                    }
                val mainState = mainViewModel.state.collectAsState()

                LaunchedEffect(key1 = Unit) {
                    mainViewModel.onEvent(Event.LoadSettings)
                }
//                region::Storage
                val networkDrive = mainState.value.settings.networkDrive
                var storageAttempt by remember { mutableStateOf(0) }
                // Репозитории создаются один раз на выбранный диск (и заново по «Повторить»), а не при каждой
                // перерисовке окна. Ошибка (нет ключа шифрования, диск недоступен) показывается окном, а не роняет программу.
                val settingsLoaded = mainState.value.settingsLoaded
                val storageResult: Result<Unit>? = remember(networkDrive, settingsLoaded, storageAttempt) {
                    if (networkDrive.isEmpty()) {
                        if (settingsLoaded) Result.failure(
                            IllegalStateException(
                                "Сетевой диск не выбран. Откройте настройки и выберите диск, на котором лежит общая база."
                            )
                        ) else null
                    } else runCatching {
                        userRepository = UserRepositoryImpl(
                            mode = Mode.ETHERNET,
                            directoryPath = mainState.value.settings.networkDrive,
                            usersDirectory = mainState.value.settings.networkDrive,
                            sessionsDirectory = mainState.value.settings.networkDrive,
                        )
                        userStorage = UserStorage(
                            getAllUsersUseCase = GetAllUsersUseCase(userRepository),
                            insertNewUserUseCase = InsertNewUserUseCase(userRepository),
                            updateUserUseCase = UpdateUserUseCase(userRepository),
                            deleteUserUseCase = DeleteUserUseCase(userRepository),
                            isPasswordUniqueUseCase = IsPasswordUniqueUseCase(userRepository),
                            getUserByLoginAndPasswordUseCase = GetUserByLoginAndPasswordUseCase(
                                userRepository
                            )
                        )

                        sessionRepository = SessionRepositoryImpl_(
                            mode = Mode.ETHERNET,
                            directoryPath = mainState.value.settings.networkDrive,
                            sessionsDirectory = mainState.value.settings.networkDrive,
                        )
                        sessionStorage = SessionStorage(
                            getAllSessionsDtoFromDatabaseUseCase = GetAllSessionsDtoFromDatabaseUseCase(sessionRepository),
                            insertOrAbortNewSessionUseCase = InsertOrAbortNewSessionUseCase(sessionRepository),
                            getSessionsWithUserIdUseCase = GetSessionsWithUserIdUseCase(sessionRepository),
                            getLastSessionIdUseCase = GetLastSessionIdUseCase(sessionRepository),
                            getSessionTrialsUseCase = GetSessionTrialsUseCase(sessionRepository),
                            getSessionMetricsUseCase = GetSessionMetricsUseCase(sessionRepository)
                        )
                    }
                }
                // Окно ошибки скрыто, пока пользователь в настройках (там выбирается сетевой диск)
                var storageErrorHidden by remember { mutableStateOf(false) }
//                endregion

                AppTheme(useDarkTheme = mainState.value.settings.isDarkTheme) {

//                    region::View models
                    val homeViewModel: HomeViewModel =
                        viewModel(modelClass = HomeViewModel::class) {
                            HomeViewModel(
                                output = { output ->
                                    when (output) {
                                        Output.AboutProgramButtonClicked -> {
                                            navigator.navigate(route = DesktopRouting.aboutapp)
                                        }

                                        Output.SettingsButtonClicked -> {
                                            navigator.navigate(route = DesktopRouting.settings)
                                        }

                                        Output.AddNewUserButtonClicked -> {
                                            navigator.navigate(route = DesktopRouting.addnewuser)
                                        }

                                        Output.UsersButtonClicked -> {
                                            navigator.navigate(route = DesktopRouting.users)
                                        }
                                    }
                                }
                            )
                        }
//                    endregion

                    NavHost(
                        navigator = navigator,
                        navTransition = NavTransition(),
                        initialRoute = DesktopRouting.home,
                        modifier = Modifier.padding(all = 0.dp)
                    ) {
//                    region::Home menu
                        scene(route = DesktopRouting.home) {
                            println("Home page rendered")
                            HomePage(homeViewModel::onEvent)
                        }
                        scene(route = DesktopRouting.settings) {
                            println("Settings page rendered")
                            SettingsPage(
                                navigator = navigator,
                                settingsStorage = settingsStorage,
                                block = {
                                    storageErrorHidden = false
                                    mainViewModel.onEvent(Event.LoadSettings)
                                }
                            )
                        }
                        scene(route = DesktopRouting.users) {
                            println("Users page rendered")
                            UsersPage(
                                navigator = navigator,
                                userStorage = userStorage,
                                block = {
                                    mainViewModel.onEvent(Event.ChangeSelectedUser(user = it))
                                }
                            )
                        }
                        scene(route = DesktopRouting.addnewuser) {
                            println("New user page rendered")
                            NewUserPage(
                                navigator = navigator,
                                userStorage = userStorage
                            )
                        }
                        scene(route = DesktopRouting.aboutuser) {
                            println("About user page created")
                            AboutUserPage(
                                navigator = navigator,
                                userStorage = userStorage,
                                sessionStorage = sessionStorage,
                                user = mainState.value.selectedUser
                            )
                        }
                        scene(route = DesktopRouting.aboutapp) {
                            println("About app page rendered")
                            AboutAppPage(navigator = navigator)
                        }
//                    endregion
                    }

                    val storageError = storageResult?.exceptionOrNull()
                    if (storageError != null && !storageErrorHidden) {
                        StorageErrorDialog(
                            message = storageErrorMessage(storageError, networkDrive),
                            appFolder = if (storageError is MissingCryptographyKeyException) {
                                File(CryptographyKeyStore.importFolderPath())
                            } else null,
                            onRetry = { storageAttempt++ },
                            onOpenSettings = {
                                storageErrorHidden = true
                                navigator.navigate(route = DesktopRouting.settings)
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Текст ошибки открытия сетевой базы. */
private fun storageErrorMessage(error: Throwable, networkDrive: String): String =
    if (error is MissingCryptographyKeyException || networkDrive.isEmpty()) error.message.orEmpty()
    else "Не удалось открыть базу на сетевом диске $networkDrive: ${error.message}. " +
        "Проверьте, что диск подключён и выбран в настройках, затем нажмите «Повторить»."
