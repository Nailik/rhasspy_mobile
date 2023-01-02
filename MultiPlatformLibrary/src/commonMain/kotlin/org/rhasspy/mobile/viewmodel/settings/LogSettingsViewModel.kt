package org.rhasspy.mobile.viewmodel.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.settings.AppSetting

class LogSettingsViewModel : ViewModel() {

    //unsaved ui data
    val logLevel = AppSetting.logLevel.data
    val isCrashlyticsEnabled = AppSetting.isCrashlyticsEnabled.data
    val isShowLogEnabled = AppSetting.isShowLogEnabled.data
    val isLogAudioFramesEnabled = AppSetting.isLogAudioFramesEnabled.data

    //all options
    val logLevelOptions = LogLevel::values

    //select log level
    fun selectLogLevel(logLevel: LogLevel) {
        AppSetting.logLevel.value = logLevel
    }

    /**
     * set if crashlytics enabled
     * changes will be detected in Application.kt and crashlytics will be enabled/disabled there
     */
    fun toggleCrashlyticsEnabled(enabled: Boolean) {
        AppSetting.isCrashlyticsEnabled.value = enabled
    }

    //toggle show log enabled
    fun toggleShowLogEnabled(enabled: Boolean) {
        AppSetting.isShowLogEnabled.value = enabled
    }

    //toggle log audio frames enabled
    fun toggleLogAudioFramesEnabled(enabled: Boolean) {
        AppSetting.isLogAudioFramesEnabled.value = enabled
    }

}