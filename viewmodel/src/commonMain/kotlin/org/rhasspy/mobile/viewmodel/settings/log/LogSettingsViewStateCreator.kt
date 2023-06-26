package org.rhasspy.mobile.viewmodel.settings.log

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class LogSettingsViewStateCreator {

    operator fun invoke(): StateFlow<LogSettingsViewState> {

        return combineStateFlow(
            AppSetting.logLevel.data,
            AppSetting.isCrashlyticsEnabled.data,
            AppSetting.isShowLogEnabled.data,
            AppSetting.isLogAudioFramesEnabled.data
        ).mapReadonlyState {
            getViewState()
        }
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