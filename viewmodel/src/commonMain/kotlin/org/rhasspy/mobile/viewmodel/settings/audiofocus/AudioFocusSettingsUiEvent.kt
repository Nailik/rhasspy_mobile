package org.rhasspy.mobile.viewmodel.settings.audiofocus

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption

@Stable
sealed interface AudioFocusSettingsUiEvent {

    @Stable
    sealed interface Change : AudioFocusSettingsUiEvent {

        @Stable
        data class SelectAudioFocusOption(val option: AudioFocusOption) : Change
        data class SetAudioFocusOnNotification(val enabled: Boolean) : Change
        data class SetAudioFocusOnSound(val enabled: Boolean) : Change
        data class SetAudioFocusOnRecord(val enabled: Boolean) : Change
        data class SetAudioFocusOnDialog(val enabled: Boolean) : Change

    }

}