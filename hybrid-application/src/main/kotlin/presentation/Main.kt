package presentation

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
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

        }
    }
}
