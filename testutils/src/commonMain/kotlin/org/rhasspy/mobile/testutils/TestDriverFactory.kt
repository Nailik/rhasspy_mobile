package org.rhasspy.mobile.testutils

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import org.rhasspy.mobile.platformspecific.database.IDriverFactory

expect class TestDriverFactory() : IDriverFactory {

    override fun createDriver(database: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver

}