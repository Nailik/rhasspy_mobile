package org.rhasspy.mobile.settings

import org.rhasspy.mobile.settings.settingsmigration.MigrateToDatabase

actual object MigrateSettingsToDatabase {

    actual fun migrateIfNecessary() {
        MigrateToDatabase.migrateIfNecessary()
    }

}