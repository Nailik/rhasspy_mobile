package org.rhasspy.mobile.viewmodel.settings.devicesettings

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class DeviceSettingsViewStateCreator {

    operator fun invoke(): StateFlow<DeviceSettingsViewState> {
        return combineStateFlow(
            AppSetting.isMqttApiDeviceChangeEnabled.data,
            AppSetting.isHttpApiDeviceChangeEnabled.data,
            AppSetting.isAudioOutputEnabled.data,
            AppSetting.isHotWordEnabled.data,
            AppSetting.isIntentHandlingEnabled.data,
            AppSetting.volume.data
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): DeviceSettingsViewState {
        return DeviceSettingsViewState(
            isMqttApiDeviceChangeEnabled = AppSetting.isMqttApiDeviceChangeEnabled.value,
            isHttpApiDeviceChangeEnabled = AppSetting.isHttpApiDeviceChangeEnabled.value,
            volume = AppSetting.volume.value,
            isHotWordEnabled = AppSetting.isHotWordEnabled.value,
            isAudioOutputEnabled = AppSetting.isAudioOutputEnabled.value,
            isIntentHandlingEnabled = AppSetting.isIntentHandlingEnabled.value
        )
    }

}