package org.rhasspy.mobile.viewmodel.settings.microphoneoverlay

import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption

sealed interface MicrophoneOverlaySettingsUiEvent {

    sealed interface Action : MicrophoneOverlaySettingsUiEvent {

        object BackClick : Action

    }

    sealed interface Change : MicrophoneOverlaySettingsUiEvent {

        data class SelectMicrophoneOverlaySizeOption(val option: MicrophoneOverlaySizeOption) :
            Change

        data class SetMicrophoneOverlayWhileAppEnabled(val enabled: Boolean) : Change

    }

}