package org.rhasspy.mobile.viewmodel.settings.devicesettings

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.*

@Stable
class DeviceSettingsSettingsViewModel(
    viewStateCreator: DeviceSettingsViewStateCreator
) : ViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: DeviceSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SetAudioOutputEnabled -> AppSetting.isAudioOutputEnabled.value = change.enabled
            is SetHotWordEnabled -> AppSetting.isHotWordEnabled.value = change.enabled
            is SetIntentHandlingEnabled -> AppSetting.isIntentHandlingEnabled.value = change.enabled
            is UpdateVolume -> AppSetting.volume.value = change.volume
        }
    }

}