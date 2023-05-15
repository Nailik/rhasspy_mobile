package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.settings.saveandrestore.SaveAndRestoreSettingsUiEvent

sealed interface MicrophoneOverlaySettingsUiEvent {

    sealed interface Navigate : MicrophoneOverlaySettingsUiEvent {
        object BackClick: Navigate
    }

    sealed interface Change : MicrophoneOverlaySettingsUiEvent {

        data class SelectMicrophoneOverlaySizeOption(val option: MicrophoneOverlaySizeOption) : Change
        data class SetMicrophoneOverlayWhileAppEnabled(val enabled: Boolean) : Change

    }

}