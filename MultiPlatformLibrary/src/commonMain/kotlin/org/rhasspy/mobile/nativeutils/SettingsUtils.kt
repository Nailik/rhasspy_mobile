package org.rhasspy.mobile.nativeutils

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SettingsUtils {

    fun saveSettingsFile()

    fun restoreSettingsFromFile()

    fun selectSoundFile(callback: (String?) -> Unit)

    fun removeSoundFile(fileName: String)

    fun selectPorcupineFile(callback: (String?) -> Unit)

    fun removePorcupineFile(fileName: String)

}