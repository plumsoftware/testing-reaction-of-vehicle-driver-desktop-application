package presentation

import AboutUserPage
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
import data.model.regular.Mode
import data.repository.SettingsRepositoryImpl
import data.repository.UserRepositoryImpl
import domain.repository.UserRepository
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
import domain.usecase.sql_database.local.GetUserByLoginAndPasswordUseCase
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
                if (mainState.value.settings.networkDrive.isNotEmpty()) {
                    userRepository = UserRepositoryImpl(
                        mode = Mode.ETHERNET,
                        directoryPath = mainState.value.settings.networkDrive,
                        usersDirectory = mainState.value.settings.networkDrive,
                        sessionsDirectory = mainState.value.settings.networkDrive,
                    )
                    userStorage = UserStorage(
                        getAllUsersUseCase = GetAllUsersUseCase(userRepository),
                        getSessionsWithUserIdUseCase = GetSessionsWithUserIdUseCase(userRepository),
                        insertNewUserUseCase = InsertNewUserUseCase(userRepository),
                        updateUserUseCase = UpdateUserUseCase(userRepository),
                        deleteUserUseCase = DeleteUserUseCase(userRepository),
                        isPasswordUniqueUseCase = IsPasswordUniqueUseCase(userRepository),
                        getUserByLoginAndPasswordUseCase = GetUserByLoginAndPasswordUseCase(
                            userRepository
                        )
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
                            AboutUserPage(navigator = navigator, userStorage = userStorage, user = mainState.value.selectedUser)
                        }
//                    endregion
                    }
                }
            }
        }
    }
}
