package org.rhasspy.mobile.settings

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory() {

    fun createDriver(): SqlDriver

}