package presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.repository.SQLDeLightRepositoryImpl
import data.repository.SettingsRepositoryImpl
import domain.model.regular.tests.ReactionTest
import domain.model.regular.tests.TrafficLight
import domain.storage.SQLDeLightStorage
import domain.storage.SettingsStorage
import domain.usecase.settings.GetUserSettingsUseCase
import domain.usecase.settings.SaveUserSettingsUseCase
import domain.usecase.sqldelight.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import presentation.home.HomePage
import presentation.home.store.Output
import presentation.home.viewmodel.HomeViewModel
import presentation.other.extension.route.DesktopRouting
import presentation.settings.SettingsPage
import presentation.settings.viewmodel.SettingsViewModel
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

//        region::Actions
        val supervisorJob = SupervisorJob()
        val coroutineContextIO: CoroutineContext = Dispatchers.IO + supervisorJob

        val settings = settingsStorage.get(scope = CoroutineScope(coroutineContextIO))
//        endregion

        val sqlDeLightRepository = SQLDeLightRepositoryImpl(networkDrive = settings.networkDrive)
        val sqlDeLightStorage = SQLDeLightStorage(
            getAllUsersUseCase = GetAllUsersUseCase(sqlDeLightRepository),
            getSessionsWithUserIdUseCase = GetSessionsWithUserIdUseCase(sqlDeLightRepository),
            insertNewUserUseCase = InsertNewUserUseCase(sqlDeLightRepository),
            updateUserUseCase = UpdateUserUseCase(sqlDeLightRepository),
            deleteUserUseCase = DeleteUserUseCase(sqlDeLightRepository)
        )
//        endregion

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("main_icon.png", ::loadImageBitmap)),
            title = "Администратор. Тест на рекацию",
            state = windowState,
        ) {
            AppTheme(useDarkTheme = settings.isDarkTheme) {
                PreComposeApp {
                    val navigator = rememberNavigator()

//                    region::View models
                    val homeViewModel: HomeViewModel = viewModel(modelClass = HomeViewModel::class) {
                        HomeViewModel(
                            output = { output ->
                                when (output) {
                                    Output.AboutProgramButtonClicked -> {

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
                    val usersViewModel: UsersViewModel = viewModel(modelClass = UsersViewModel::class) {
                        UsersViewModel(
                            output = { output ->
                                when (output) {
                                    presentation.users.store.Output.BackButtonClicked -> {
                                        navigator.popBackStack()
                                    }
                                }
                            },
                            sqlDeLightStorage = sqlDeLightStorage,
                            coroutineContextIO = coroutineContextIO
                        )
                    }
                    val settingsViewModel: SettingsViewModel = viewModel(modelClass = SettingsViewModel::class) {
                        SettingsViewModel(
                            settingsStorage = settingsStorage,
                            coroutineContextIO = coroutineContextIO,
                            output = { output ->
                                when (output) {
                                    presentation.settings.store.Output.BackClicked -> {
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
//                    region::Home menu
                        scene(route = DesktopRouting.home) {
                            println("Home page rendered")
                            HomePage(homeViewModel::onEvent)
                        }
                        scene(route = DesktopRouting.users) {
                            println("Users page rendered")
                            UsersPage(onEvent = usersViewModel::onEvent, usersViewModel)
                        }
                        scene(route = DesktopRouting.settings) {
                            println("Settings page rendered")
                            SettingsPage(
                                onEvent = settingsViewModel::onEvent,
                                settingsViewModel = settingsViewModel
                            )
                        }
//                    endregion
                    }
                }
            }
        }
    }
}
