package org.rhasspy.mobile.viewmodel.settings.devicesettings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow

class DeviceSettingsViewStateUpdater(
    private val _viewState: MutableStateFlow<DeviceSettingsViewState>
) {

    private val updaterScope = CoroutineScope(Dispatchers.Default)

    init {
        //live update when settings change from mqtt/ webserver
        updaterScope.launch(Dispatchers.Default) {
            combineStateFlow(
                AppSetting.isAudioOutputEnabled.data,
                AppSetting.isHotWordEnabled.data,
                AppSetting.isIntentHandlingEnabled.data,
                AppSetting.volume.data
            ).collect { _viewState.value = DeviceSettingsViewState() }
        }
    }

}