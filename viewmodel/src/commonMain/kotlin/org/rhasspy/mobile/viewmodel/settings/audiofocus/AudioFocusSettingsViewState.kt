package org.rhasspy.mobile.viewmodel.settings.audiofocus

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import org.rhasspy.mobile.data.audiofocus.AudioFocusOption
import org.rhasspy.mobile.platformspecific.features.FeatureAvailability

@Stable
data class AudioFocusSettingsViewState(
    val audioFocusOption: AudioFocusOption,
    val isAudioFocusOnNotification: Boolean,
    val isAudioFocusOnSound: Boolean,
    val isAudioFocusOnRecord: Boolean,
    val isAudioFocusOnDialog: Boolean,
   //TODO moved to mic domain val isPauseRecordingOnMedia: Boolean,
) {

    val audioFocusOptions: ImmutableList<AudioFocusOption> = AudioFocusOption.entries.toImmutableList()

    val isPauseRecordingOnMediaFeatureEnabled = FeatureAvailability.isPauseRecordingOnPlaybackFeatureEnabled //TODO maybe use it for audio lauter machen as well

}