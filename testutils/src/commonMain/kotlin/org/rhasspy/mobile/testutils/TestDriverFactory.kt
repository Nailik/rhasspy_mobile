package org.rhasspy.mobile.testutils

import app.cash.sqldelight.db.SqlDriver
import org.rhasspy.mobile.settings.IDriverFactory

expect class TestDriverFactory() : IDriverFactory {

    override fun createDriver(): SqlDriver

}