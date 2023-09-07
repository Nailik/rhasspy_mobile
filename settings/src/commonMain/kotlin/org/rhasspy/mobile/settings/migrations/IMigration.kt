package org.rhasspy.mobile.settings.migrations

abstract class IMigration(
    val from: Int,
    val to: Int
) {

    fun migrateIfNecessary(currentVersion: Int): Int {
        if (currentVersion == from) {
            migrate()
            return to
        }
        return currentVersion
    }

    abstract fun migrate()

}