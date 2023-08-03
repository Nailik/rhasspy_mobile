package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class MicrophoneOverlaySettingsViewStateCreator {

    operator fun invoke(): StateFlow<MicrophoneOverlaySettingsViewState> {

        return combineStateFlow(
            AppSetting.microphoneOverlaySizeOption.data,
            AppSetting.isMicrophoneOverlayWhileAppEnabled.data
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): MicrophoneOverlaySettingsViewState {
        return MicrophoneOverlaySettingsViewState(
            microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
            isMicrophoneOverlayWhileAppEnabled = AppSetting.isMicrophoneOverlayWhileAppEnabled.value
        )
    }

}