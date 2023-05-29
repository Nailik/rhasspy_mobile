package org.rhasspy.mobile.viewmodel.settings.log

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting

class LogSettingsViewStateCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): StateFlow<LogSettingsViewState> {
        val viewState = MutableStateFlow(getViewState())
        //live update when settings change from mqtt/ webserver
        updaterScope.launch {
            combineStateFlow(
                AppSetting.logLevel.data,
                AppSetting.isCrashlyticsEnabled.data,
                AppSetting.isShowLogEnabled.data,
                AppSetting.isLogAudioFramesEnabled.data
            ).collect { viewState.value = getViewState() }
        }
        return viewState
    }

    private fun getViewState(): LogSettingsViewState {
        return LogSettingsViewState(
            logLevel = AppSetting.logLevel.value,
            isCrashlyticsEnabled = AppSetting.isCrashlyticsEnabled.value,
            isShowLogEnabled = AppSetting.isShowLogEnabled.value,
            isLogAudioFramesEnabled = AppSetting.isLogAudioFramesEnabled.value
        )
    }

}