package data.repository

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.CryptographyRepositoryImpl
import data.KeyScope
import data.constant.DatabaseConstants
import data.constant.GeneralConstants
import data.model.dto.database.SessionDTO
import data.model.dto.test.InputDevice
import data.model.dto.test.PreTestState
import data.model.dto.test.ResponseHand
import data.model.dto.test.TestEnvironment
import data.model.dto.test.TestMetric
import data.model.dto.test.TrialOutcome
import data.model.dto.test.TrialRecord
import data.model.dto.test.VisionCorrection
import data.model.regular.Mode
import data.model.regular.user.DrivingLicenseCategory
import data.model.regular.user.Handedness
import data.model.regular.user.Interval
import domain.repository.SessionRepository
import ru.plumsoftware.Database
import ru.plumsoftware.sessions.GetLastSessionId
import ru.plumsoftware.sessions.Sessions
import utlis.createFolderIfNotExists
import utlis.getSessionsDatabaseDriver

class SessionRepositoryImpl_(
    mode: Mode = Mode.SINGLE,
    directoryPath: String = GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER,
    sessionsDirectory: String = DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME
) : SessionRepository {

    // Ключ к этому моменту уже есть: UserRepositoryImpl создаётся раньше и при необходимости его генерирует.
    private val cryptographyRepository = CryptographyRepositoryImpl(
        scope = if (mode == Mode.ETHERNET) KeyScope.NETWORK else KeyScope.LOCAL
    )

    private val sessionsDatabaseDriver: JdbcSqliteDriver
    private val dir = if (mode == Mode.SINGLE) GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER else GeneralConstants.Paths.PATH_TO_ROAMING_DATABASE_DIRECTORY(directoryPath)

    init {
        createFolderIfNotExists(directoryPath = dir)

        sessionsDatabaseDriver = if (mode == Mode.ETHERNET) getSessionsDatabaseDriver(networkDrive = sessionsDirectory) else JdbcSqliteDriver(
            url = DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME
        )

        val sessionsDatabase =
            Database(driver = sessionsDatabaseDriver)
        sessionsDatabase.sqldelight_sessions_schemeQueries.create()
        sessionsDatabase.sqldelight_sessions_schemeQueries.createTrials()
        sessionsDatabase.sqldelight_sessions_schemeQueries.createMetrics()
        addMissingColumns()
        migrateStandardDeviationToSeconds()
    }

    private fun addMissingColumns() {
        val existing = sessionsDatabaseDriver.executeQuery(
            identifier = null,
            sql = "PRAGMA table_info(Sessions)",
            mapper = { cursor ->
                val names = mutableListOf<String>()
                while (cursor.next().value) {
                    cursor.getString(1)?.let { names.add(it) }
                }
                QueryResult.Value(names)
            },
            parameters = 0
        ).value

        addedColumns.filter { (name, _) -> name !in existing }.forEach { (name, type) ->
            sessionsDatabaseDriver.execute(
                identifier = null,
                sql = "ALTER TABLE Sessions ADD COLUMN $name $type",
                parameters = 0
            )
        }
    }

    /**
     * До версии 1 стандартное отклонение сохранялось в миллисекундах, а среднее — в секундах.
     * Однократно переводит старые записи в секунды; версия схемы хранится в PRAGMA user_version.
     */
    private fun migrateStandardDeviationToSeconds() {
        val version = sessionsDatabaseDriver.executeQuery(
            identifier = null,
            sql = "PRAGMA user_version",
            mapper = { cursor ->
                QueryResult.Value(if (cursor.next().value) cursor.getLong(0) ?: 0L else 0L)
            },
            parameters = 0
        ).value

        if (version < SCHEMA_VERSION_SD_IN_SECONDS) {
            sessionsDatabaseDriver.execute(
                identifier = null,
                sql = "UPDATE Sessions SET standard_deviation = standard_deviation / 1000.0",
                parameters = 0
            )
            sessionsDatabaseDriver.execute(
                identifier = null,
                sql = "PRAGMA user_version = $SCHEMA_VERSION_SD_IN_SECONDS",
                parameters = 0
            )
        }
    }

    private companion object {
        const val SCHEMA_VERSION_SD_IN_SECONDS = 1L

        /**
         * Столбцы, добавленные после первой версии (методика 2, затем время начала/окончания и тренировка). В БД, созданных раньше, таблица Sessions уже существует
         * (CREATE TABLE IF NOT EXISTS её не меняет), поэтому недостающие столбцы добавляются здесь.
         */
        val addedColumns = listOf(
            "valid_count" to "INTEGER",
            "wrong_responses" to "INTEGER",
            "omissions" to "INTEGER",
            "premature_responses" to "INTEGER",
            "handedness" to "TEXT",
            "user_session_number" to "INTEGER",
            "vision_correction" to "TEXT",
            "sleep_hours" to "TEXT",
            "well_being" to "TEXT",
            "response_hand" to "TEXT",
            "input_device" to "TEXT",
            "monitor_name" to "TEXT",
            "monitor_size_cm" to "TEXT",
            "monitor_diagonal_inches" to "REAL",
            "screen_resolution" to "TEXT",
            "refresh_rate_hz" to "INTEGER",
            "scale_percent" to "INTEGER",
            "test_window_px" to "TEXT",
            "computer_name" to "TEXT",
            "operating_system" to "TEXT",
            "app_version" to "TEXT",
            "method_version" to "TEXT",
            "started_at" to "TEXT",
            "main_started_at" to "TEXT",
            "finished_at" to "TEXT",
            "with_practice" to "INTEGER",
        )
    }

    override suspend fun getAllSessionDtoFromDatabase(): List<SessionDTO> {
        val database = Database(driver = sessionsDatabaseDriver)
        return database.sqldelight_sessions_schemeQueries.selectAllSessions().executeAsList()
            .map { toSessionDto(it) }
    }

    override suspend fun getSessionsWithUserId(id: Long): List<SessionDTO> {
        val database = Database(driver = sessionsDatabaseDriver)
        return database.sqldelight_sessions_schemeQueries.getSessionsWithUserId(user_id = id).executeAsList()
            .map { toSessionDto(it) }
    }

    override suspend fun insertOrAbortNewSession(
        sessionDTO: SessionDTO,
        trials: List<TrialRecord>,
        metrics: List<TestMetric>
    ): Long {
        val database = Database(driver = sessionsDatabaseDriver)
        val queries = database.sqldelight_sessions_schemeQueries
        with(sessionDTO) {

            // Персональные данные и сведения о состоянии здоровья шифруются, как и прежде
            val encodedExperience = cryptographyRepository.encode(text = experience.toLong().toString())
            val encodedAge = cryptographyRepository.encode(text = userAge.toLong().toString())
            val encodedDrivingLicence = cryptographyRepository.encode(text = drivingLicenseCategory.toString())
            val encodedHandedness = encodeOrNull(handedness.takeIf { it != Handedness.UNKNOWN }?.toString())
            val encodedVision = encodeOrNull(preTestState.visionCorrection?.name)
            val encodedSleep = encodeOrNull(preTestState.sleepHours?.toString())
            val encodedWellBeing = encodeOrNull(preTestState.wellBeing?.toString())

            // id берётся через last_insert_rowid() в той же транзакции: MAX(session_id) в сетевом режиме
            // может вернуть сессию, параллельно сохранённую с другого компьютера
            return queries.transactionWithResult {
                queries.insertOrAbortNewSession(
                    user_id = userId,
                    test_id = testId,
                    test_year = testYear.toLong(),
                    test_month = testMonth.toLong(),
                    test_day = testDay.toLong(),
                    test_hour_of_day_24h = testHourOfDay24h.toLong(),
                    test_minute_of_hour = testMinuteOfHour.toLong(),
                    average_value = averageValue,
                    standard_deviation = standardDeviation,
                    count = count.toLong(),
                    errors = errors.toLong(),
                    experience = encodedExperience,
                    user_age = encodedAge,
                    driving_license_category = encodedDrivingLicence,
                    signal_interval = signalInterval.toString(),
                    valid_count = validCount?.toLong(),
                    wrong_responses = wrongResponses?.toLong(),
                    omissions = omissions?.toLong(),
                    premature_responses = prematureResponses?.toLong(),
                    handedness = encodedHandedness,
                    user_session_number = userSessionNumber?.toLong(),
                    vision_correction = encodedVision,
                    sleep_hours = encodedSleep,
                    well_being = encodedWellBeing,
                    response_hand = preTestState.responseHand?.name,
                    input_device = preTestState.inputDevice?.name,
                    monitor_name = environment?.monitorName,
                    monitor_size_cm = environment?.monitorSizeCm,
                    monitor_diagonal_inches = environment?.monitorDiagonalInches,
                    screen_resolution = environment?.screenResolution,
                    refresh_rate_hz = environment?.refreshRateHz?.toLong(),
                    scale_percent = environment?.scalePercent?.toLong(),
                    test_window_px = environment?.testWindowPx,
                    computer_name = environment?.computerName,
                    operating_system = environment?.operatingSystem,
                    app_version = environment?.appVersion,
                    method_version = environment?.methodVersion,
                    started_at = startedAt,
                    main_started_at = mainStartedAt,
                    finished_at = finishedAt,
                    with_practice = withPractice?.let { if (it) 1L else 0L }
                )
                val newSessionId = queries.lastInsertRowId().executeAsOne()
                trials.forEach { trial ->
                    queries.insertTrial(
                        session_id = newSessionId,
                        trial_number = trial.number.toLong(),
                        stimulus = trial.stimulus,
                        foreperiod_ms = trial.foreperiodMs,
                        outcome = trial.outcome.name,
                        reaction_time_ms = trial.reactionTimeMs
                    )
                }
                metrics.forEach { metric ->
                    queries.insertMetric(
                        session_id = newSessionId,
                        metric_key = metric.key,
                        metric_name = metric.name,
                        metric_value = metric.value,
                        metric_unit = metric.unit
                    )
                }
                newSessionId
            }
        }
    }

    override suspend fun getTrials(sessionId: Long): List<TrialRecord> {
        val database = Database(driver = sessionsDatabaseDriver)
        return database.sqldelight_sessions_schemeQueries.getTrialsBySessionId(session_id = sessionId).executeAsList()
            .mapNotNull { row ->
                val outcome = runCatching { TrialOutcome.valueOf(row.outcome) }.getOrNull() ?: return@mapNotNull null
                TrialRecord(
                    number = row.trial_number.toInt(),
                    stimulus = row.stimulus,
                    foreperiodMs = row.foreperiod_ms,
                    outcome = outcome,
                    reactionTimeMs = row.reaction_time_ms
                )
            }
    }

    override suspend fun getMetrics(sessionId: Long): List<TestMetric> {
        val database = Database(driver = sessionsDatabaseDriver)
        return database.sqldelight_sessions_schemeQueries.getMetricsBySessionId(session_id = sessionId).executeAsList()
            .map { row ->
                TestMetric(key = row.metric_key, name = row.metric_name, value = row.metric_value, unit = row.metric_unit)
            }
    }

    override suspend fun getLastSessionId(): Long {
        val driver = sessionsDatabaseDriver
        val database = Database(driver = driver)
        val executeAsList: List<GetLastSessionId> = database.sqldelight_sessions_schemeQueries.getLastSessionId().executeAsList()
        return executeAsList[0].MAX ?: 0L
    }

    private suspend fun toSessionDto(row: Sessions): SessionDTO = with(row) {
        val methodVersion = method_version
        SessionDTO(
            sessionId = session_id,
            userId = user_id,
            testId = test_id,
            testYear = test_year.toInt(),
            testMonth = test_month.toInt(),
            testDay = test_day.toInt(),
            testHourOfDay24h = test_hour_of_day_24h.toInt(),
            testMinuteOfHour = test_minute_of_hour.toInt(),
            averageValue = average_value,
            standardDeviation = standard_deviation,
            count = count.toInt(),
            errors = errors.toInt(),
            experience = cryptographyRepository.decode(text = experience),
            userAge = cryptographyRepository.decode(text = user_age),
            drivingLicenseCategory = DrivingLicenseCategory.fromString(cryptographyRepository.decode(text = driving_license_category)),
            signalInterval = Interval.fromString(signal_interval),
            validCount = valid_count?.toInt(),
            wrongResponses = wrong_responses?.toInt(),
            omissions = omissions?.toInt(),
            prematureResponses = premature_responses?.toInt(),
            handedness = decodeOrNull(handedness)?.let { Handedness.fromString(it) } ?: Handedness.UNKNOWN,
            userSessionNumber = user_session_number?.toInt(),
            preTestState = PreTestState(
                visionCorrection = decodeOrNull(vision_correction)
                    ?.let { runCatching { VisionCorrection.valueOf(it) }.getOrNull() },
                sleepHours = decodeOrNull(sleep_hours)?.toIntOrNull(),
                wellBeing = decodeOrNull(well_being)?.toIntOrNull(),
                responseHand = response_hand?.let { runCatching { ResponseHand.valueOf(it) }.getOrNull() },
                inputDevice = input_device?.let { runCatching { InputDevice.valueOf(it) }.getOrNull() },
            ),
            environment = if (methodVersion == null) null else TestEnvironment(
                monitorName = monitor_name ?: "",
                monitorSizeCm = monitor_size_cm ?: "",
                monitorDiagonalInches = monitor_diagonal_inches,
                screenResolution = screen_resolution ?: "",
                refreshRateHz = refresh_rate_hz?.toInt(),
                scalePercent = scale_percent?.toInt(),
                testWindowPx = test_window_px ?: "",
                computerName = computer_name ?: "",
                operatingSystem = operating_system ?: "",
                appVersion = app_version ?: "",
                methodVersion = methodVersion
            ),
            startedAt = started_at,
            mainStartedAt = main_started_at,
            finishedAt = finished_at,
            withPractice = with_practice?.let { it != 0L }
        )
    }

    private suspend fun encodeOrNull(text: String?): String? =
        text?.let { cryptographyRepository.encode(text = it) }

    private suspend fun decodeOrNull(text: String?): String? =
        if (text.isNullOrBlank()) null else cryptographyRepository.decode(text = text)
}
