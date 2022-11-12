package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.settings.AppSettings

class LogSettingsViewModel : ViewModel() {

    //unsaved ui data
    val logLevel = AppSettings.logLevel.data
    val isShowLogEnabled = AppSettings.isShowLogEnabled.data
    val isLogAudioFramesEnabled = AppSettings.isLogAudioFramesEnabled.data

    //all options
    val logLevelOptions = LogLevel::values

    //select log level
    fun selectLogLevel(logLevel: LogLevel) {
        AppSettings.logLevel.value = logLevel
    }

    //toggle show log enabled
    fun toggleShowLogEnabled(enabled: Boolean) {
        AppSettings.isShowLogEnabled.value = enabled
    }

    //toggle log audio frames enabled
    fun toggleLogAudioFramesEnabled(enabled: Boolean) {
        AppSettings.isLogAudioFramesEnabled.value = enabled
    }

}