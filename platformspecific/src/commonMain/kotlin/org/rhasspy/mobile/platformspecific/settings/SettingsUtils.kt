package org.rhasspy.mobile.platformspecific.settings

expect object SettingsUtils {

    /**
     * export the settings file
     */
    suspend fun exportSettingsFile(): Boolean

    /**
     * restore all settings from a file
     */
    suspend fun restoreSettingsFromFile(): Boolean

    /**
     * share settings file but without sensitive data
     */
    suspend fun shareSettingsFile(): Boolean

}