package org.rhasspy.mobile.viewModels.settings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.logger.LogLevel
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.AppSettings

class LogSettingsViewModel : ViewModel() {

    //unsaved data
    private val _logLevel = MutableStateFlow(AppSettings.logLevel.value)
    private val _isShowLogEnabled = MutableStateFlow(AppSettings.isShowLogEnabled.value)
    private val _isLogAudioFramesEnabled = MutableStateFlow(AppSettings.isLogAudioFramesEnabled.value)

    //unsaved ui data
    val logLevel = _logLevel.readOnly
    val isShowLogEnabled = _isShowLogEnabled.readOnly
    val isLogAudioFramesEnabled = _isLogAudioFramesEnabled.readOnly

    //all options
    val logLevelOptions = LogLevel::values

    //select log level
    fun selectLogLevel(logLevel: LogLevel) {
        _logLevel.value = logLevel
    }

    //toggle show log enabled
    fun toggleShowLogEnabled(enabled: Boolean) {
        _isShowLogEnabled.value = enabled
    }

    //toggle log audio frames enabled
    fun toggleLogAudioFramesEnabled(enabled: Boolean) {
        _isLogAudioFramesEnabled.value = enabled
    }

    /**
     * save data configuration
     */
    fun save() {
        AppSettings.logLevel.value = _logLevel.value
        AppSettings.isShowLogEnabled.value = _isShowLogEnabled.value
        AppSettings.isLogAudioFramesEnabled.value = _isLogAudioFramesEnabled.value
    }

}