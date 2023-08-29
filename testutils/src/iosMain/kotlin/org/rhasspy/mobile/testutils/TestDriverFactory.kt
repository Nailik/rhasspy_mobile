package org.rhasspy.mobile.testutils

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.rhasspy.mobile.platformspecific.database.IDriverFactory

actual class TestDriverFactory : IDriverFactory {

    actual override fun createDriver(database: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver {
        return NativeSqliteDriver(database, name)
    }

}