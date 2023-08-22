package org.rhasspy.mobile.testutils

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.rhasspy.mobile.settings.Database
import org.rhasspy.mobile.settings.IDriverFactory

actual class TestDriverFactory : IDriverFactory {

    actual override fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
            Database.Schema.create(it)
        }
    }

}