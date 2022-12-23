package org.rhasspy.mobile.nativeutils

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SettingsUtils {

    fun saveSettingsFile()

    fun restoreSettingsFromFile()

}