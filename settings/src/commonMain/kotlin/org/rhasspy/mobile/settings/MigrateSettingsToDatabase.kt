package org.rhasspy.mobile.settings

import org.rhasspy.mobile.platformspecific.application.NativeApplication

expect object MigrateSettingsToDatabase {

    fun migrateIfNecessary(nativeApplication: NativeApplication)

}