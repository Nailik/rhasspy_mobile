package org.rhasspy.mobile.viewmodel.settings.devicesettings

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.SetAudioOutputEnabled
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.SetHotWordEnabled
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.SetHttpApiChangesEnabled
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.SetIntentHandlingEnabled
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.SetMqttApiChangesEnabled
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.UpdateVolume

@Stable
class DeviceSettingsViewModel(
    viewStateCreator: DeviceSettingsViewStateCreator
) : ScreenViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: DeviceSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetMqttApiChangesEnabled -> AppSetting.isMqttApiDeviceChangeEnabled.value =
                change.enabled

            is SetHttpApiChangesEnabled -> AppSetting.isHttpApiDeviceChangeEnabled.value =
                change.enabled

            is SetAudioOutputEnabled -> AppSetting.isAudioOutputEnabled.value = change.enabled
            is SetHotWordEnabled -> AppSetting.isHotWordEnabled.value = change.enabled
            is SetIntentHandlingEnabled -> AppSetting.isIntentHandlingEnabled.value = change.enabled
            is UpdateVolume -> AppSetting.volume.value = change.volume
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            is BackClick -> navigator.onBackPressed()
        }
    }

}