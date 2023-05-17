package org.rhasspy.mobile.viewmodel.settings.devicesettings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting

class DeviceSettingsViewStateCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): StateFlow<DeviceSettingsViewState> {
        val viewState = MutableStateFlow(getViewState())
        //live update when settings change from mqtt/ webserver
        updaterScope.launch {
            combineStateFlow(
                AppSetting.isAudioOutputEnabled.data,
                AppSetting.isHotWordEnabled.data,
                AppSetting.isIntentHandlingEnabled.data,
                AppSetting.volume.data
            ).collect { viewState.value = getViewState() }
        }
        return viewState
    }

    private fun getViewState(): DeviceSettingsViewState {
        return DeviceSettingsViewState(
            volume = AppSetting.volume.value,
            isHotWordEnabled = AppSetting.isHotWordEnabled.value,
            isAudioOutputEnabled = AppSetting.isAudioOutputEnabled.value,
            isIntentHandlingEnabled = AppSetting.isIntentHandlingEnabled.value
        )
    }

}