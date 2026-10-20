package data.repository

import HashRepositoryImpl
import app.cash.sqldelight.db.QueryResult
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
        addHandednessColumnIfMissing()

        val sessionsDatabase =
            Database(driver = sessionsDatabaseDriver)
        sessionsDatabase.sqldelight_users_schemeQueries.create()
    }

    override suspend fun getAllUsers(): List<Users> {
        val database =
            Database(driver = usersDatabaseDriver)
        val userList =
            database.sqldelight_users_schemeQueries.getAllUsers().executeAsList().reversed()
                .map { decodeRow(it) }
        return userList
    }

    @Throws(Exception::class)
    override suspend fun insertNewUser(user: User) {
        val database =
            Database(driver = usersDatabaseDriver)
        val passwordHash = hashRepository.hash(text = user.password)
        val encodedGender = cryptographyRepository.encode(user.gender.toString())
        val encodedHandedness = cryptographyRepository.encode(user.handedness.toString())
        with(encodeUser(user = user)) {
            database.sqldelight_users_schemeQueries.transaction {
                database.sqldelight_users_schemeQueries.insertNewUser(
                    user_login = login,
                    user_password = passwordHash,
                    user_name = name,
                    user_surname = surname,
                    user_patronymic = patronymic,
                    gender = encodedGender,
                    handedness = encodedHandedness,
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
        val encodedHandedness = cryptographyRepository.encode(user.handedness.toString())
        with(encodedUser) {
            database.sqldelight_users_schemeQueries.updateUser(
                user_login = login,
                user_password = passwordHash,
                user_name = name,
                user_surname = surname,
                user_patronymic = patronymic,
                gender = encodedGender,
                handedness = encodedHandedness,
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
                    .map { decodeRow(it) }

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

    /** Расшифровывает поле, которое может быть пустым (например, у записей до добавления столбца). */
    private suspend fun decodeOptional(text: String?): String {
        if (text.isNullOrBlank()) return ""
        return cryptographyRepository.decode(text = text)
    }

    private suspend fun decodeRow(row: Users): Users = row.copy(
        user_name = decode(text = row.user_name),
        user_surname = decode(text = row.user_surname),
        user_patronymic = decodeOptional(row.user_patronymic),
        gender = decode(text = row.gender),
        handedness = decodeOptional(row.handedness)
    )

    /** Добавляет столбец handedness в таблицы, созданные до его появления. */
    private fun addHandednessColumnIfMissing() {
        val columns = usersDatabaseDriver.executeQuery(
            identifier = null,
            sql = "PRAGMA table_info(Users)",
            mapper = { cursor ->
                val names = mutableListOf<String>()
                while (cursor.next().value) {
                    cursor.getString(1)?.let { names.add(it) }
                }
                QueryResult.Value(names)
            },
            parameters = 0
        ).value

        if ("handedness" !in columns) {
            usersDatabaseDriver.execute(
                identifier = null,
                sql = "ALTER TABLE Users ADD COLUMN handedness TEXT NOT NULL DEFAULT ''",
                parameters = 0
            )
        }
    }
}