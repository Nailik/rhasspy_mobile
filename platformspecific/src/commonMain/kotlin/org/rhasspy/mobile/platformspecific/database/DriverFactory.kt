package org.rhasspy.mobile.platformspecific.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

interface IDriverFactory {

    fun createDriver(database: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver

}


expect class DriverFactory() : IDriverFactory {

    override fun createDriver(database: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver

}