package org.rhasspy.mobile.settings

import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.settings.settingsmigration.MigrateToDatabase

actual object MigrateSettingsToDatabase {

    actual fun migrateIfNecessary(nativeApplication: NativeApplication) {
        MigrateToDatabase.migrateIfNecessary(nativeApplication)
    }

}