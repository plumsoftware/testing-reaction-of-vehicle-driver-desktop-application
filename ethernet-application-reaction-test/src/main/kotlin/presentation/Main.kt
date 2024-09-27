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
import authorization.auth.AuthorizationPage
import data.repository.SessionRepositoryImpl
import data.repository.SettingsRepositoryImpl
import data.repository.UserRepositoryImpl
import data.repository.WorkbookRepositoryImpl
import domain.model.regular.tests.TrafficLight
import domain.model.regular.tests.ReactionTest
import domain.storage.SessionStorage
import domain.storage.SettingsStorage
import domain.storage.UserStorage
import domain.storage.WorkbookStorage
import domain.usecase.settings.GetUserSettingsUseCase
import domain.usecase.settings.SaveUserSettingsUseCase
import domain.usecase.sql_database.local.GetAllSessionsDtoFromDatabaseUseCase
import domain.usecase.sql_database.local.GetLastSessionIdUseCase
import domain.usecase.sql_database.local.InsertOrAbortNewSessionUseCase
import domain.usecase.sql_database.roaming.GetUserByLoginAndPasswordUseCase
import domain.usecase.sql_database.roaming.InsertOrAbortNewSessionToRoamingDatabaseUseCase
import domain.usecase.workbook.CreateWorkbookIfNotExistsUseCase
import domain.usecase.workbook.WriteDataToWorkbookUseCase
import kotlinx.coroutines.*
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import presentation.authorization.login.Login
import presentation.authorization.login.viewmodel.LoginViewModel
import presentation.home.HomePage
import presentation.home.store.Output
import presentation.home.viewmodel.HomeViewModel
import presentation.main.MainViewModel
import presentation.other.extension.route.DesktopRouting
import presentation.settings.SettingsPage
import presentation.settings.viewmodel.SettingsViewModel
import presentation.testmenu.TestMenu
import presentation.testmenu.viewmodel.TestMenuViewModel
import presentation.tests.traffic_light_test.TrafficLightTest
import presentation.tests.traffic_light_test.store.Event
import presentation.tests.traffic_light_test.viewmodel.TrafficLightTestViewModel
import presentation.theme.AppTheme
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
//        endregion

//        region::Actions
        val supervisorJob = SupervisorJob()
        val coroutineContextIO: CoroutineContext = Dispatchers.IO + supervisorJob

        val settings = settingsStorage.get(scope = CoroutineScope(coroutineContextIO))
//        endregion

//        region::Storage
        val userRepository = UserRepositoryImpl(netDriver = settings.networkDrive)
        val userStorage = UserStorage(
            getUserByLoginAndPasswordUseCase = GetUserByLoginAndPasswordUseCase(userRepository)
        )
        val sessionRepository = SessionRepositoryImpl(netDriver = settings.networkDrive)
        val sessionStorage = SessionStorage(
            getAllSessionsDtoFromDatabaseUseCase = GetAllSessionsDtoFromDatabaseUseCase(sessionRepository),
            insertOrAbortNewSessionUseCase = InsertOrAbortNewSessionUseCase(sessionRepository),
            getLastSessionIdUseCase = GetLastSessionIdUseCase(sessionRepository),
            insertOrAbortNewSessionToRoamingDatabaseUseCase = InsertOrAbortNewSessionToRoamingDatabaseUseCase(
                sessionRepository
            )
        )
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
                                    Output.AboutProgramButtonClicked -> {

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
                    testMenuViewModel = viewModel(modelClass = TestMenuViewModel::class) {
                        TestMenuViewModel(
                            output = { output ->
                                when (output) {
                                    is presentation.testmenu.store.Output.TestClicked -> {
                                        trafficLightTestViewModel?.onEvent(Event.InitTestMode(testMode = output.testMode))
                                        navigator.navigate(route = output.route)
                                    }

                                    presentation.testmenu.store.Output.BackButtonClicked -> {
                                        navigator.goBack()
                                    }
                                }
                            }
                        )
                    }
//                    val authorizationViewModel: AuthorizationViewModel =
//                        viewModel(modelClass = AuthorizationViewModel::class) {
//                            AuthorizationViewModel(
//                                output = { output ->
//                                    when (output) {
//                                        presentation.authorization.auth.store.Output.BackButtonClicked -> {
//                                            navigator.goBack()
//                                        }
//                                    }
//                                }
//                            )
//                        }
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
                        scene(route = DesktopRouting.auth) {
                            println("Authorization page rendered")
                            AuthorizationPage(navigator = navigator)
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
//                        endregion

//                        region::Tests
                        scene(route = DesktopRouting.trafficLight) {
                            println("TrafficLightTest page rendered")
//                            trafficLightTestViewModel.collectActions()
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
