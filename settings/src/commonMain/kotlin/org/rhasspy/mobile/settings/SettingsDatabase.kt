package org.rhasspy.mobile.settings

class SettingsDatabase {

    private val driver = DriverFactory().createDriver()
    val database = Database(driver)

}