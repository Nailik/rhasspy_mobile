package org.rhasspy.mobile.nativeutils

expect object SettingsUtils {

    fun saveSettingsFile()

    fun restoreSettingsFromFile()

    fun selectSoundFile(callback: (String) -> Unit)

    fun selectPorcupineFile(callback: (String) -> Unit)

}