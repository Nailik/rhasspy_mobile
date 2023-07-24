package org.rhasspy.mobile.viewmodel.settings.audiorecorder

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.platformspecific.toImmutableList

@Stable
data class AudioRecorderSettingsViewState internal constructor(
    val audioRecorderChannelType: AudioRecorderChannelType,
    val audioRecorderEncodingType: AudioRecorderEncodingType,
    val audioRecorderSampleRateType: AudioRecorderSampleRateType
) {

    val audioRecorderChannelTypes: ImmutableList<AudioRecorderChannelType> =
        AudioRecorderChannelType.values().toImmutableList()
    val audioRecorderEncodingTypes: ImmutableList<AudioRecorderEncodingType> =
        AudioRecorderEncodingType.supportedValues().toImmutableList()
    val audioRecorderSampleRateTypes: ImmutableList<AudioRecorderSampleRateType> =
        AudioRecorderSampleRateType.values().toImmutableList()

}