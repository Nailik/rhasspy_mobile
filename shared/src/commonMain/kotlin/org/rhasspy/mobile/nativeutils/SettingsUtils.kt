package org.rhasspy.mobile.nativeutils

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SettingsUtils {

    /**
     * export the settings file
     */
    fun exportSettingsFile()

    /**
     * restore all settings from a file
     */
    fun restoreSettingsFromFile()

    /**
     * share settings file but without sensitive data
     */
    fun shareSettingsFile()

}