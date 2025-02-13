package data.repository

import HashRepositoryImpl
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.CryptographyRepositoryImpl
import data.constant.DatabaseConstants
import data.constant.GeneralConstants
import data.model.either.local.LocalEither
import data.model.regular.Mode
import domain.repository.UserRepository
import ru.plumsoftware.Database
import ru.plumsoftware.users.Users
import utlis.createFolderIfNotExists
import data.model.regular.user.User
import utlis.getSessionsDatabaseDriver
import utlis.getUsersDatabaseDriver

class UserRepositoryImpl(
    mode: Mode = Mode.SINGLE,
    directoryPath: String = GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER,
    usersDirectory: String = DatabaseConstants.LOCAL_USER_JDBC_DRIVER_NAME,
    sessionsDirectory: String = DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME
) : UserRepository {

    private val hashRepository = HashRepositoryImpl()
    private val cryptographyRepository = CryptographyRepositoryImpl()

    private val usersDatabaseDriver: JdbcSqliteDriver
    private val sessionsDatabaseDriver: JdbcSqliteDriver
    private val dir =
        if (mode == Mode.SINGLE) GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER else GeneralConstants.Paths.PATH_TO_ROAMING_DATABASE_DIRECTORY(
            directoryPath
        )

    init {
        createFolderIfNotExists(directoryPath = dir)

        usersDatabaseDriver =
            if (mode == Mode.ETHERNET) getUsersDatabaseDriver(networkDrive = usersDirectory) else JdbcSqliteDriver(
                url = DatabaseConstants.LOCAL_USER_JDBC_DRIVER_NAME
            )

        sessionsDatabaseDriver =
            if (mode == Mode.ETHERNET) getSessionsDatabaseDriver(networkDrive = sessionsDirectory) else JdbcSqliteDriver(
                url = DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME
            )

        val userDatabase =
            Database(driver = usersDatabaseDriver)
        userDatabase.sqldelight_users_schemeQueries.create()

        val sessionsDatabase =
            Database(driver = sessionsDatabaseDriver)
        sessionsDatabase.sqldelight_users_schemeQueries.create()
    }

    override suspend fun getAllUsers(): List<Users> {
        val database =
            Database(driver = usersDatabaseDriver)
        val userList =
            database.sqldelight_users_schemeQueries.getAllUsers().executeAsList().reversed()
                .map {
                    val user_name = decode(text = it.user_name)
                    val user_surname = decode(text = it.user_surname)
                    val user_patronymic = decode(text = it.user_patronymic ?: "")
                    val user_gender = cryptographyRepository.encode(text = it.gender)
                    it.copy(user_name = user_name, user_surname = user_surname, user_patronymic = user_patronymic, gender = user_gender)
                }
        return userList
    }

    @Throws(Exception::class)
    override suspend fun insertNewUser(user: User) {
        val database =
            Database(driver = usersDatabaseDriver)
        val passwordHash = hashRepository.hash(text = user.password)
        val encodedGender = cryptographyRepository.encode(user.gender.toString())
        with(encodeUser(user = user)) {
            database.sqldelight_users_schemeQueries.transaction {
                database.sqldelight_users_schemeQueries.insertNewUser(
                    user_login = login,
                    user_password = passwordHash,
                    user_name = name,
                    user_surname = surname,
                    user_patronymic = patronymic,
                    gender = encodedGender,
                )
            }
        }
    }

    override suspend fun getAllPasswords(): List<String> {

        val database =
            Database(driver = usersDatabaseDriver)
        val selectAllPasswords =
            database.sqldelight_users_schemeQueries.selectAllPasswords().executeAsList()

        return selectAllPasswords
    }

    override suspend fun updateUser(user: User) {
        val database =
            Database(driver = usersDatabaseDriver)
        val passwordHash = hashRepository.hash(text = user.password)
        val encodedUser = encodeUser(user = user)
        val encodedGender = cryptographyRepository.encode(user.gender.toString())
        with(encodedUser) {
            database.sqldelight_users_schemeQueries.updateUser(
                user_login = login,
                user_password = passwordHash,
                user_name = name,
                user_surname = surname,
                user_patronymic = patronymic,
                gender = encodedGender,
                user_id = id
            )
        }
    }

    override suspend fun deleteUser(id: Long) {
        val database =
            Database(driver = usersDatabaseDriver)
        database.sqldelight_users_schemeQueries.deleteUser(user_id = id)
    }

    override suspend fun getUserByLoginAndPassword(
        login: String,
        password: String
    ): LocalEither<Exception, List<Users>> {
        val database =
            Database(driver = usersDatabaseDriver)
        val passwordHash = hashRepository.hash(text = password)
        try {
            val executeAsList: List<Users> =
                database.sqldelight_users_schemeQueries.getUserByLoginAndPassword(
                    user_login = login,
                    user_password = passwordHash
                ).executeAsList()

            return LocalEither.Data(executeAsList)
        } catch (e: Exception) {
            return LocalEither.Exception(e)
        }
    }

    private suspend inline fun encodeUser(user: User): User {
        val encodedName = cryptographyRepository.encode(text = user.name)
        val encodedSurname = cryptographyRepository.encode(text = user.surname)
        val encodedPatronymic = cryptographyRepository.encode(text = user.patronymic)

        return user.copy(
            name = encodedName,
            surname = encodedSurname,
            patronymic = encodedPatronymic
        )
    }

    private suspend inline fun decode(text: String): String {
        return cryptographyRepository.decode(text = text)
    }
}