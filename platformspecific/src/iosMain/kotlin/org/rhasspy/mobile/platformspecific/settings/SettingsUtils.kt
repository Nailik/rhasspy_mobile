package org.rhasspy.mobile.platformspecific.settings

actual object SettingsUtils {
    /**
     * export the settings file
     */
    actual suspend fun exportSettingsFile(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    /**
     * restore all settings from a file
     */
    actual suspend fun restoreSettingsFromFile(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    /**
     * share settings file but without sensitive data
     */
    actual suspend fun shareSettingsFile(): Boolean {
        //TODO("Not yet implemented")
        return true
    }
}