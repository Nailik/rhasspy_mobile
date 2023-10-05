package org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.platformspecific.toImmutableList

@Stable
data class AudioRecorderFormatConfigurationViewState internal constructor(
    val editData: AudioRecorderFormatConfigurationData,
) {

    @Stable
    data class AudioRecorderFormatConfigurationData internal constructor(
        val audioInputChannel: AudioFormatChannelType,
        val audioInputEncoding: AudioFormatEncodingType,
        val audioInputSampleRate: AudioFormatSampleRateType,
    ) {

        val audioFormatChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.entries.toTypedArray().toImmutableList()
        val audioFormatEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
        val audioFormatSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.entries.toTypedArray().toImmutableList()

    }

}