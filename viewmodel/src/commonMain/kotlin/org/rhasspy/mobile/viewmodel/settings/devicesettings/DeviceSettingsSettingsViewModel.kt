package org.rhasspy.mobile.viewmodel.settings.devicesettings

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.devicesettings.DeviceSettingsUiEvent.Change.*

class DeviceSettingsSettingsViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(DeviceSettingsViewState())
    val viewState = _viewState.readOnly

    fun onEvent(event: DeviceSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            when (change) {
                is SetAudioOutputEnabled -> {
                    AppSetting.isAudioOutputEnabled.value = change.enabled
                    it.copy(isAudioOutputEnabled = change.enabled)
                }

                is SetHotWordEnabled -> {
                    AppSetting.isHotWordEnabled.value = change.enabled
                    it.copy(isHotWordEnabled = change.enabled)
                }

                is SetIntentHandlingEnabled -> {
                    AppSetting.isIntentHandlingEnabled.value = change.enabled
                    it.copy(isIntentHandlingEnabled = change.enabled)
                }

                is UpdateVolume -> {
                    AppSetting.volume.value = change.volume
                    it.copy(volume = change.volume)
                }
            }
        }
    }

    init {
        //live update when settings change from mqtt/ webserver
        viewModelScope.launch(Dispatchers.Default) {
            combineStateFlow(
                AppSetting.isAudioOutputEnabled.data,
                AppSetting.isHotWordEnabled.data,
                AppSetting.isIntentHandlingEnabled.data,
                AppSetting.volume.data
            ).collect { data ->
                _viewState.update {
                    it.copy(
                        volume = data[0] as Float,
                        isHotWordEnabled = data[1] as Boolean,
                        isAudioOutputEnabled = data[2] as Boolean,
                        isIntentHandlingEnabled = data[3] as Boolean
                    )
                }
            }
        }
    }

}