package org.rhasspy.mobile.viewmodel.settings.audiofocus

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption

@Stable
data class AudioFocusSettingsViewState(
    val audioFocusOption: AudioFocusOption,
    val isAudioFocusOnNotification: Boolean,
    val isAudioFocusOnSound: Boolean,
    val isAudioFocusOnRecord: Boolean,
    val isAudioFocusOnDialog: Boolean
) {

    val audioFocusOptions: PersistentList<AudioFocusOption> = AudioFocusOption.values().toMutableList().toPersistentList()

}