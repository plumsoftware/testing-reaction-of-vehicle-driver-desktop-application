package presentation

import about_app.AboutAppPage
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
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
import data.repository.WorkbookRepositoryImpl
import domain.repository.SessionRepository
import domain.repository.UserRepository
import domain.storage.SessionStorage
import domain.storage.SettingsStorage
import domain.storage.UserStorage
import domain.storage.WorkbookStorage
import domain.usecase.settings.GetUserSettingsUseCase
import domain.usecase.settings.SaveUserSettingsUseCase
import domain.usecase.sql_database.local.GetAllSessionsDtoFromDatabaseUseCase
import domain.usecase.sql_database.local.GetLastSessionIdUseCase
import domain.usecase.sql_database.local.GetUserByLoginAndPasswordUseCase
import domain.usecase.sql_database.local.InsertOrAbortNewSessionUseCase
import domain.usecase.sql_database.local.user.DeleteUserUseCase
import domain.usecase.sql_database.local.user.GetAllUsersUseCase
import domain.usecase.sql_database.local.user.GetSessionsWithUserIdUseCase
import domain.usecase.sql_database.local.user.InsertNewUserUseCase
import domain.usecase.sql_database.local.user.IsPasswordUniqueUseCase
import domain.usecase.sql_database.local.user.UpdateUserUseCase
import domain.usecase.workbook.CreateWorkbookIfNotExistsUseCase
import domain.usecase.workbook.WriteDataToWorkbookUseCase
import newuser.auth.AuthorizationPage
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import newuser.login.Login
import other.extension.route.DesktopRouting
import presentation.home.HomePage
import presentation.home.store.Output
import presentation.home.viewmodel.HomeViewModel
import presentation.main.MainViewModel
import presentation.main.store.Event
import presentation.settings.SettingsPage
import testmenu.TestMenu
import tests.traffic_light_test.TrafficLightTest
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
        val workbookRepository = WorkbookRepositoryImpl()
        val workBookStorage = WorkbookStorage(
            createWorkbookIfNotExistsUseCase = CreateWorkbookIfNotExistsUseCase(workbookRepository),
            writeDataToWorkbookUseCase = WriteDataToWorkbookUseCase(workbookRepository)
        )
//        endregion

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("main_icon.png", ::loadImageBitmap)),
            title = "Тест на рекацию",
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
                if (mainState.value.settings.networkDrive.isNotEmpty()) {
                    userRepository = UserRepositoryImpl(
                        mode = Mode.ETHERNET,
                        directoryPath = mainState.value.settings.networkDrive,
                        usersDirectory = mainState.value.settings.networkDrive,
                        sessionsDirectory = mainState.value.settings.networkDrive,
                    )
                    sessionRepository = SessionRepositoryImpl_(
                        mode = Mode.ETHERNET,
                        directoryPath = mainState.value.settings.networkDrive,
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
                    sessionStorage = SessionStorage(
                        getAllSessionsDtoFromDatabaseUseCase = GetAllSessionsDtoFromDatabaseUseCase(
                            sessionRepository = sessionRepository
                        ),
                        insertOrAbortNewSessionUseCase = InsertOrAbortNewSessionUseCase(
                            sessionRepository = sessionRepository
                        ),
                        getSessionsWithUserIdUseCase = GetSessionsWithUserIdUseCase(
                            sessionRepository
                        ),
                        getLastSessionIdUseCase = GetLastSessionIdUseCase(sessionRepository = sessionRepository)
                    )
                }

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

                                        Output.TestsButtonClicked -> {
                                            navigator.navigate(route = DesktopRouting.login)
                                        }
                                    }
                                }
                            )
                        }

                    NavHost(
                        navigator = navigator,
                        navTransition = NavTransition(),
                        initialRoute = DesktopRouting.home,
                        modifier = Modifier.padding(all = 0.dp)
                    ) {
//                        region::Home menu
                        scene(route = DesktopRouting.home) {
                            println("Home page rendered")
                            HomePage(homeViewModel::onEvent)
                        }
                        scene(route = DesktopRouting.testmenu) {
                            println("Test menu page rendered")
                            TestMenu(
                                navigator = navigator,
                                testDto = mainState.value.testDTO,
                                block = {
                                    mainViewModel.onEvent(
                                        Event.ChangeTestDto(
                                            testDto = it
                                        )
                                    )
                                }
                            )
                        }
                        scene(route = DesktopRouting.auth) {
                            println("Authorization page rendered")
                            AuthorizationPage(navigator = navigator)
                        }
                        scene(route = DesktopRouting.settings) {
                            println("Settings page rendered")
                            SettingsPage(
                                navigator = navigator,
                                settingsStorage = settingsStorage,
                                block = {
                                    mainViewModel.onEvent(Event.LoadSettings)
                                }
                            )
                        }
                        scene(route = DesktopRouting.login) {
                            println("Login page rendered")
                            Login(
                                navigator = navigator,
                                userStorage = userStorage,
                                block = {
                                    mainViewModel.onEvent(
                                        Event.ChangeTestDto(
                                            testDto = it
                                        )
                                    )
                                }
                            )
                        }
                        scene(route = DesktopRouting.aboutapp) {
                            println("About app page rendered")
                            AboutAppPage(navigator = navigator)
                        }
//                        endregion

//                        region::Tests
                        scene(route = DesktopRouting.trafficLight) {
                            println("TrafficLightTest page rendered")
                            TrafficLightTest(
                                workBookStorage = workBookStorage,
                                settings = mainState.value.settings,
                                testDTO = mainState.value.testDTO,
                                sessionStorage = sessionStorage,
                                navigator = navigator
                            )
                        }
//                        endregion
                    }
                }
            }
        }
    }
}
