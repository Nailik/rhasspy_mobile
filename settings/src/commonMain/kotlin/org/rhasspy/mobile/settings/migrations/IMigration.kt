package org.rhasspy.mobile.settings.migrations

import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

abstract class IMigration(
    val from: Int,
    val to: Int
) : KoinComponent {

    protected val settings = get<Settings>()

    open fun preMigrate() {}

    fun migrateIfNecessary(currentVersion: Int): Int {
        if (currentVersion == from) {
            preMigrate()
            migrate()
            return to
        }
        return currentVersion
    }

    abstract fun migrate()

}