package org.rhasspy.mobile.viewmodel.settings.devicesettings

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.*

@Stable
class DeviceSettingsSettingsViewModel(
    viewStateCreator: DeviceSettingsViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: DeviceSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
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

    private fun onAction(action: Action) {
        when (action) {
            is BackClick -> navigator.popBackStack()
        }
    }

}