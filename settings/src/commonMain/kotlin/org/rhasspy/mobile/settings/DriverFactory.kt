package org.rhasspy.mobile.settings

import app.cash.sqldelight.db.SqlDriver

interface IDriverFactory {

    fun createDriver(): SqlDriver

}

expect class DriverFactory() : IDriverFactory {

    override fun createDriver(): SqlDriver

}