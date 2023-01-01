package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.nativeutils.SettingsUtils

class SaveAndRestoreSettingsViewModel : ViewModel() {

    /**
     * export settings to a zip file and save on device
     */
    fun exportSettingsFile() = SettingsUtils.exportSettingsFile()

    /**
     * restore all settings from zip file
     */
    fun restoreSettingsFromFile() = SettingsUtils.restoreSettingsFromFile()

    /**
     * share settings file without sensitive data
     */
    fun shareSettingsFile() = SettingsUtils.shareSettingsFile()

}