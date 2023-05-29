package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting

class MicrophoneOverlaySettingsViewStateCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): StateFlow<MicrophoneOverlaySettingsViewState> {
        val viewState = MutableStateFlow(getViewState())
        //live update when settings change from mqtt/ webserver
        updaterScope.launch {
            combineStateFlow(
                AppSetting.microphoneOverlaySizeOption.data,
                AppSetting.isMicrophoneOverlayWhileAppEnabled.data
            ).collect { viewState.value = getViewState() }
        }
        return viewState
    }

    private fun getViewState(): MicrophoneOverlaySettingsViewState {
        return MicrophoneOverlaySettingsViewState(
            microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
            isMicrophoneOverlayWhileAppEnabled = AppSetting.isMicrophoneOverlayWhileAppEnabled.value
        )
    }

}