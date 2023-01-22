package org.rhasspy.mobile.viewmodel.settings

import co.touchlab.kermit.Logger
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
    fun restoreSettingsFromFile() {
        try {
            SettingsUtils.restoreSettingsFromFile()
        } catch (exception: Throwable) {
            Logger.withTag("SaveAndRestoreSettingsViewModel").e(exception) { "restoreSettingsFromFile" }
        }
    }

    /**
     * share settings file without sensitive data
     */
    fun shareSettingsFile() = SettingsUtils.shareSettingsFile()

}