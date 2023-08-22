package org.rhasspy.mobile.platformspecific.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration

actual class DriverFactory : IDriverFactory {

    actual override fun createDriver(database: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver {
        return NativeSqliteDriver(
            schema = database,
            name = name,
            onConfiguration = { config: DatabaseConfiguration ->
                config.copy(
                    extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
                )
            }
        )
    }

}