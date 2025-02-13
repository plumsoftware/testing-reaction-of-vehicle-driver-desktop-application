package utlis

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.constant.DatabaseConstants

internal fun getUsersDatabaseDriver(networkDrive: String): JdbcSqliteDriver {
    val path = DatabaseConstants.collapseNetDriver(netDriver = networkDrive)
    val driver = JdbcSqliteDriver(path)
    return driver
}

internal fun getSessionsDatabaseDriver(networkDrive: String): JdbcSqliteDriver {
    val path = DatabaseConstants.collapseNetDriverToSessions(netDriver = networkDrive)
    val driver = JdbcSqliteDriver(path)
    return driver
}