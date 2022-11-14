package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.nativeutils.SettingsUtils

class SaveAndRestoreSettingsViewModel : ViewModel() {

    fun saveSettingsFile() = SettingsUtils.saveSettingsFile()

    fun restoreSettingsFromFile() = SettingsUtils.restoreSettingsFromFile()

    fun shareSettingsFile() = SettingsUtils.saveSettingsFile() //TODO

}