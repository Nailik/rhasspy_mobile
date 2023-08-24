package org.rhasspy.mobile.settings

import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.platformspecific.database.IDriverFactory

class ISettingsDatabase : KoinComponent {

    val logger = Logger.withTag("ISettingsDatabase")

    private val driver = get<IDriverFactory>().createDriver(SettingsDatabase.Schema, "settings.db")
    val database = SettingsDatabase(driver)

    init {
        logger.d { "init" }
    }

    fun close() {
        driver.close()
    }

}