package org.rhasspy.mobile.settings

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual class DriverFactory : KoinComponent {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, get<NativeApplication>(), "settings.db")
    }

}