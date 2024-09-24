package presentation

import androidx.compose.foundation.layout.padding
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
import data.repository.SessionRepositoryImpl
import data.repository.SettingsRepositoryImpl
import data.repository.UserRepositoryImpl
import data.repository.WorkbookRepositoryImpl
import domain.model.regular.tests.ReactionTest
import domain.model.regular.tests.TrafficLight
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import presentation.aboutuser.AboutUserPage
import presentation.aboutuser.viewmodel.AboutUserViewModel
import presentation.authorization.login.Login
import presentation.authorization.login.viewmodel.LoginViewModel
import presentation.home.HomePage
import presentation.home.viewmodel.HomeViewModel
import presentation.main.MainViewModel
import presentation.newuser.NewUserPage
import presentation.newuser.viewmodel.NewUserViewModel
import presentation.other.extension.route.DesktopRouting
import presentation.privacypolicy.PrivacyPolicy
import presentation.privacypolicy.viewmodel.PrivacyPolicyViewModel
import presentation.settings.SettingsPage
import presentation.settings.viewmodel.SettingsViewModel
import presentation.testmenu.TestMenu
import presentation.testmenu.store.Output
import presentation.testmenu.viewmodel.TestMenuViewModel
import presentation.tests.traffic_light_test.TrafficLightTest
import presentation.tests.traffic_light_test.store.Event
import presentation.tests.traffic_light_test.viewmodel.TrafficLightTestViewModel
import presentation.theme.AppTheme
import presentation.users.UsersPage
import presentation.users.viewmodel.UsersViewModel
import kotlin.coroutines.CoroutineContext

fun main() = run {

    println("App started!")

    application {

        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
        val reactionTests: List<ReactionTest> = listOf(
            TrafficLight()
        )

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
        val userRepository = UserRepositoryImpl()
        val userStorage = UserStorage(
            getAllUsersUseCase = GetAllUsersUseCase(userRepository),
            getSessionsWithUserIdUseCase = GetSessionsWithUserIdUseCase(userRepository),
            insertNewUserUseCase = InsertNewUserUseCase(userRepository),
            updateUserUseCase = UpdateUserUseCase(userRepository),
            deleteUserUseCase = DeleteUserUseCase(userRepository),
            isPasswordUniqueUseCase = IsPasswordUniqueUseCase(userRepository),
            getUserByLoginAndPasswordUseCase = GetUserByLoginAndPasswordUseCase(userRepository)
        )
        val sessionRepository = SessionRepositoryImpl()
        val sessionStorage = SessionStorage(
            getLastSessionIdUseCase = GetLastSessionIdUseCase(sessionRepository),
            getAllSessionsDtoFromDatabaseUseCase = GetAllSessionsDtoFromDatabaseUseCase(sessionRepository),
            insertOrAbortNewSessionUseCase = InsertOrAbortNewSessionUseCase(sessionRepository)
        )
//        endregion

//        region::Actions
        val supervisorJob = SupervisorJob()
        val coroutineContextIO: CoroutineContext = Dispatchers.IO + supervisorJob

        val settings = settingsStorage.get(scope = CoroutineScope(coroutineContextIO))
//        endregion

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("main_icon.png", ::loadImageBitmap)),
            title = "Тест на рекацию",
            state = windowState,
        ) {
            AppTheme(useDarkTheme = settings.isDarkTheme) {
                PreComposeApp {
//                    region::View models
                    val navigator = rememberNavigator()
                    val testMenuViewModel: TestMenuViewModel
                    var trafficLightTestViewModel: TrafficLightTestViewModel? = null

                    val mainViewModel: MainViewModel = viewModel(modelClass = MainViewModel::class) {
                        MainViewModel()
                    }
                    val homeViewModel: HomeViewModel = viewModel(modelClass = HomeViewModel::class) {
                        HomeViewModel(
                            output = { output ->
                                when (output) {
                                    presentation.home.store.Output.AboutProgramButtonClicked -> {

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
                    testMenuViewModel = viewModel(modelClass = TestMenuViewModel::class) {
                        TestMenuViewModel(
                            output = { output ->
                                when (output) {
                                    is Output.TestClicked -> {
                                        trafficLightTestViewModel?.onEvent(Event.InitTestMode(testMode = output.testMode))
                                        navigator.navigate(route = output.route)
                                    }

                                    Output.BackButtonClicked -> {
                                        navigator.goBack()
                                    }
                                }
                            }
                        )
                    }
                    val settingsViewModel: SettingsViewModel = viewModel(modelClass = SettingsViewModel::class) {
                        SettingsViewModel(
                            settingsStorage = settingsStorage,
                            coroutineContextIO = coroutineContextIO,
                            output = { output ->
                                when (output) {
                                    presentation.settings.store.Output.BackClicked -> navigator.goBack()
                                }
                            }
                        )
                    }
                    val loginViewModel: LoginViewModel = viewModel(modelClass = LoginViewModel::class) {
                        LoginViewModel(
                            output = { output ->
                                when (output) {
                                    presentation.authorization.login.store.Output.BackButtonClicked -> navigator.goBack()

                                    is presentation.authorization.login.store.Output.OpenTestMenu -> {
                                        testMenuViewModel.onEvent(presentation.testmenu.store.Event.ChangeUser(user = output.testDTO.user))
                                        trafficLightTestViewModel?.onEvent(Event.InitStartData(testDTO = output.testDTO))
                                        navigator.navigate(route = DesktopRouting.testmenu)
                                    }

                                    presentation.authorization.login.store.Output.OpenPrivacyPolicy -> navigator.navigate(route = DesktopRouting.privacyPolicy)
                                }
                            },
                            userStorage = userStorage
                        )
                    }
                    trafficLightTestViewModel = viewModel(modelClass = TrafficLightTestViewModel::class) {
                        TrafficLightTestViewModel(
                            workbookStorage = workBookStorage,
                            dataFormats = settings.dataFormats,
                            localFolderToTable = settings.localFolderToTable,
                            output = { output ->
                                when (output) {
                                    presentation.tests.traffic_light_test.store.Output.BackButtonClicked -> {
                                        navigator.navigate(route = DesktopRouting.home)
                                    }
                                }
                            },
                            actions = mainViewModel.trafficLightActions,
                            sessionStorage = sessionStorage
                        )
                    }

                    val aboutUserViewModel: AboutUserViewModel = viewModel(modelClass = AboutUserViewModel::class) {
                        AboutUserViewModel(
                            userStorage = userStorage,
                            coroutineContextIO = coroutineContextIO,
                            output = { output ->
                                when (output) {
                                    presentation.aboutuser.store.Output.BackButtonClicked -> {
                                        navigator.popBackStack()
                                    }
                                }
                            }
                        )
                    }

                    val privacyPolicyViewModel = viewModel(modelClass = PrivacyPolicyViewModel::class) {
                        PrivacyPolicyViewModel(
                            output = { output ->
                                when (output) {
                                    presentation.privacypolicy.store.Output.BackButtonClicked -> navigator.popBackStack()
                                }
                            }
                        )
                    }

                    val usersViewModel: UsersViewModel = viewModel(modelClass = UsersViewModel::class) {
                        UsersViewModel(
                            userStorage = userStorage,
                            output = { output ->
                                when (output) {
                                    presentation.users.store.Output.BackButtonClicked -> {
                                        navigator.popBackStack()
                                    }

                                    is presentation.users.store.Output.OnUserClicked -> {
                                        aboutUserViewModel.onEvent(
                                            presentation.aboutuser.store.Event.ChangeSelectedUser(
                                                userId = output.userId
                                            )
                                        )
                                        navigator.navigate(route = DesktopRouting.aboutuser)
                                    }
                                }
                            }
                        )
                    }

                    val newUserViewModel: NewUserViewModel = viewModel(modelClass = NewUserViewModel::class) {
                        NewUserViewModel(
                            userStorage = userStorage,
                            coroutineContextIO = coroutineContextIO,
                            output = { output ->
                                when (output) {
                                    presentation.newuser.store.Output.BackButtonClicked -> {
                                        navigator.popBackStack()
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
//                        region::Home menu
                        scene(route = DesktopRouting.home) {
                            println("Home page rendered")
                            HomePage(homeViewModel::onEvent)
                        }
                        scene(route = DesktopRouting.testmenu) {
                            println("Test menu page rendered")
                            TestMenu(testMenuViewModel::onEvent, reactionTests, testMenuViewModel)
                        }
                        scene(route = DesktopRouting.settings) {
                            println("Settings page rendered")
                            SettingsPage(settingsViewModel::onEvent, settingsViewModel.state)
                        }
                        scene(route = DesktopRouting.login) {
                            println("Login page rendered")
                            Login(
                                onEvent = loginViewModel::onEvent,
                                state = loginViewModel.state.collectAsState(),
                            )
                        }
                        scene(route = DesktopRouting.users) {
                            println("Users page rendered")
                            UsersPage(
                                onEvent = usersViewModel::onEvent,
                                onAction = usersViewModel::onAction,
                                usersViewModel = usersViewModel
                            )
                        }
                        scene(route = DesktopRouting.addnewuser) {
                            println("New user page rendered")
                            NewUserPage(
                                onEvent = newUserViewModel::onEvent,
                                newUserViewModel = newUserViewModel
                            )
                        }
                        scene(route = DesktopRouting.aboutuser) {
                            println("About user page created")
                            AboutUserPage(onEvent = aboutUserViewModel::onEvent, aboutUserViewModel)
                        }
//                        endregion

//                        region::Privacy policy
                        scene(route = DesktopRouting.privacyPolicy) {
                            PrivacyPolicy(onEvent = privacyPolicyViewModel::onEvent)
                        }
//                        endregion

//                        region::Tests
                        scene(route = DesktopRouting.trafficLight) {
                            println("TrafficLightTest page rendered")
                            TrafficLightTest(
                                trafficLightTestViewModel::onEvent,
                                trafficLightTestViewModel.state,
                                getAverage = trafficLightTestViewModel::getAverage,
                                getStdDeviation = trafficLightTestViewModel::getStdDeviation,
                                mainViewModel::onEvent
                            )
                        }
//                        endregion
                    }
                }
            }
        }
    }
}
