package org.rhasspy.mobile.viewmodel.settings.audiofocus

import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent

sealed interface AudioFocusSettingsUiEvent {

    sealed interface Navigate : AudioFocusSettingsUiEvent {
        object BackClick: Navigate
    }

    sealed interface Change : AudioFocusSettingsUiEvent {

        data class SelectAudioFocusOption(val option: AudioFocusOption) : Change
        data class SetAudioFocusOnNotification(val enabled: Boolean) : Change
        data class SetAudioFocusOnSound(val enabled: Boolean) : Change
        data class SetAudioFocusOnRecord(val enabled: Boolean) : Change
        data class SetAudioFocusOnDialog(val enabled: Boolean) : Change

    }

}