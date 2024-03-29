package org.rhasspy.mobile.viewmodel.settings.audiofocus

import org.rhasspy.mobile.data.audiofocus.AudioFocusOption

sealed interface AudioFocusSettingsUiEvent {

    sealed interface Action : AudioFocusSettingsUiEvent {
        data object BackClick : Action
    }

    sealed interface Change : AudioFocusSettingsUiEvent {

        data class SelectAudioFocusOption(val option: AudioFocusOption) : Change
        data class SetAudioFocusOnNotification(val enabled: Boolean) : Change
        data class SetAudioFocusOnSound(val enabled: Boolean) : Change
        data class SetAudioFocusOnRecord(val enabled: Boolean) : Change
        data class SetAudioFocusOnDialog(val enabled: Boolean) : Change
        data class SetStopRecording(val enabled: Boolean) : Change

    }

}