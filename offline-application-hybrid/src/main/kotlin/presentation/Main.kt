package presentation

import AboutUserPage
import about_app.AboutAppPage
import allusers.UsersPage
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
import newuser.login.Login
import data.repository.SessionRepositoryImpl_
import data.repository.SettingsRepositoryImpl
import data.repository.UserRepositoryImpl
import data.repository.WorkbookRepositoryImpl
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
import domain.usecase.sql_database.local.user.UpdateUserUseCase
import domain.usecase.sql_database.local.user.GetSessionsWithUserIdUseCase
import domain.usecase.sql_database.local.user.InsertNewUserUseCase
import domain.usecase.sql_database.local.user.IsPasswordUniqueUseCase
import domain.usecase.workbook.CreateWorkbookIfNotExistsUseCase
import domain.usecase.workbook.WriteDataToWorkbookUseCase
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import newuser.NewUserPage
import presentation.home.HomePage
import presentation.home.viewmodel.HomeViewModel
import theme.AppTheme
import other.extension.route.DesktopRouting
import presentation.main.MainViewModel
import presentation.main.store.Event
import privacypolicy.PrivacyPolicy
import presentation.settings.SettingsPage
import testmenu.TestMenu
import tests.traffic_light_test.TrafficLightTest

fun main() = run {

    println("App started!")

    application {

        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

//        region::Storage
        /** Integrated **/
        val userRepository = UserRepositoryImpl()
        val userStorage = UserStorage(
            getAllUsersUseCase = GetAllUsersUseCase(userRepository),
            insertNewUserUseCase = InsertNewUserUseCase(userRepository),
            updateUserUseCase = UpdateUserUseCase(userRepository),
            deleteUserUseCase = DeleteUserUseCase(userRepository),
            isPasswordUniqueUseCase = IsPasswordUniqueUseCase(userRepository),
            getUserByLoginAndPasswordUseCase = GetUserByLoginAndPasswordUseCase(userRepository)
        )

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
        val sessionRepository = SessionRepositoryImpl_()
        val sessionStorage = SessionStorage(
            getLastSessionIdUseCase = GetLastSessionIdUseCase(sessionRepository),
            getAllSessionsDtoFromDatabaseUseCase = GetAllSessionsDtoFromDatabaseUseCase(
                sessionRepository
            ),
            getSessionsWithUserIdUseCase = GetSessionsWithUserIdUseCase(sessionRepository),
            insertOrAbortNewSessionUseCase = InsertOrAbortNewSessionUseCase(sessionRepository)
        )
//        endregion

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("main_icon.png", ::loadImageBitmap)),
            title = "Тест на реакцию",
            state = windowState,
        ) {
            PreComposeApp {
//                region::View models
                val navigator = rememberNavigator()

                val mainViewModel: MainViewModel =
                    viewModel(modelClass = MainViewModel::class) {
                        MainViewModel(settingsStorage = settingsStorage)
                    }
                val mainState = mainViewModel.state.collectAsState()

                LaunchedEffect(key1 = Unit) {
                    mainViewModel.onEvent(Event.LoadSettings)
                }

                val homeViewModel: HomeViewModel =
                    viewModel(modelClass = HomeViewModel::class) {
                        HomeViewModel(
                            output = { output ->
                                when (output) {
                                    presentation.home.store.Output.AboutProgramButtonClicked -> {
                                        navigator.navigate(route = DesktopRouting.aboutapp)
                                    }

                                    presentation.home.store.Output.SettingsButtonClicked -> {
                                        navigator.navigate(route = DesktopRouting.settings)
                                    }

                                    presentation.home.store.Output.TestsButtonClicked -> {
                                        navigator.navigate(route = DesktopRouting.login)
                                    }

                                    presentation.home.store.Output.AddNewUserButtonClicked -> {
                                        navigator.navigate(route = DesktopRouting.addnewuser)
                                    }

                                    presentation.home.store.Output.UsersButtonClicked -> {
                                        navigator.navigate(route = DesktopRouting.users)
                                    }
                                }
                            }
                        )
                    }
//                    endregion
                AppTheme(useDarkTheme = mainState.value.settings.isDarkTheme) {
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
//                        endregion

//                        region::Privacy policy
                        scene(route = DesktopRouting.privacyPolicy) {
                            PrivacyPolicy()
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
