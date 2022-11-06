package org.rhasspy.mobile.nativeutils

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SettingsUtils {

    fun saveSettingsFile()

    fun restoreSettingsFromFile()

    fun selectSoundFile(subfolder: String, callback: (String?) -> Unit)

    fun removeSoundFile(subfolder: String, fileName: String)

    fun selectPorcupineFile(callback: (String?) -> Unit)

    fun removePorcupineFile(fileName: String)

}