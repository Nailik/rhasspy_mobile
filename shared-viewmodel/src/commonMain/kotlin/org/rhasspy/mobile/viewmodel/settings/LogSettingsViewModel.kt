package org.rhasspy.mobile.viewmodel.settings

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.logger.LogLevel
import org.rhasspy.mobile.logic.nativeutils.NativeApplication
import org.rhasspy.mobile.logic.settings.AppSetting

class LogSettingsViewModel : ViewModel(), KoinComponent {

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
        Logger.setMinSeverity(AppSetting.logLevel.value.severity)
    }

    /**
     * set if crashlytics enabled
     * changes will be detected in Application.kt and crashlytics will be enabled/disabled there
     */
    fun toggleCrashlyticsEnabled(enabled: Boolean) {
        AppSetting.isCrashlyticsEnabled.value = enabled
        get<NativeApplication>().setCrashlyticsCollectionEnabled(AppSetting.isCrashlyticsEnabled.value)
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