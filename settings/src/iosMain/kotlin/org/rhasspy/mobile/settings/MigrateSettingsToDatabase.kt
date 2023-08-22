package org.rhasspy.mobile.settings

import org.rhasspy.mobile.platformspecific.application.NativeApplication

actual object MigrateSettingsToDatabase {

    actual fun migrateIfNecessary(nativeApplication: NativeApplication) {}

}