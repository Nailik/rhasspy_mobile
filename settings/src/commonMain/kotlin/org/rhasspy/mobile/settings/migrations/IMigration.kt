package org.rhasspy.mobile.settings.migrations

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

abstract class IMigration(
    val from: Int,
    val to: Int
) : KoinComponent {

    private val logger = Logger.withTag("IMigration")

    protected val settings = get<Settings>()

    open fun preMigrate() {}

    fun migrateIfNecessary(currentVersion: Int): Int {
        logger.d { "migrateIfNecessary currentVersion $currentVersion from $from" }
        if (currentVersion == from) {
            preMigrate()
            migrate()
            return to
        }
        return currentVersion
    }

    abstract fun migrate()

}