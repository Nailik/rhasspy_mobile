package org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.platformspecific.toImmutableList

@Stable
data class AudioOutputFormatConfigurationViewState internal constructor(
    val editData: AudioOutputFormatConfigurationData,
) {

    @Stable
    data class AudioOutputFormatConfigurationData internal constructor(
        val audioOutputChannel: AudioFormatChannelType,
        val audioOutputEncoding: AudioFormatEncodingType,
        val audioOutputSampleRate: AudioFormatSampleRateType,
    ) {

        val audioFormatChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.entries.toTypedArray().toImmutableList()
        val audioFormatEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
        val audioFormatSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.entries.toTypedArray().toImmutableList()

    }

    //TODO disable encoding change for android
}