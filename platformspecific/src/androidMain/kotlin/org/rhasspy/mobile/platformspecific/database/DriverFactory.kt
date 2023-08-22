package org.rhasspy.mobile.platformspecific.database

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class DriverFactory : IDriverFactory, KoinComponent {
    actual override fun createDriver(database: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver {

        return AndroidSqliteDriver(
            schema = database,
            context = get<NativeApplication>(),
            factory = RequerySQLiteOpenHelperFactory(),
            name = name,
            callback = object : AndroidSqliteDriver.Callback(database) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }

}