package org.rhasspy.mobile.settings

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class DriverFactory : IDriverFactory, KoinComponent {
    actual override fun createDriver(): SqlDriver {

        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = get<NativeApplication>(),
            factory = RequerySQLiteOpenHelperFactory(),
            name = "settings.db",
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }

}