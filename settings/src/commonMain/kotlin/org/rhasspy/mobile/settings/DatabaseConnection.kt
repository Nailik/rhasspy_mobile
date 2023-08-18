package org.rhasspy.mobile.settings

class DatabaseConnection {

    private val driver = DriverFactory().createDriver()
    val database = Database(driver)

}