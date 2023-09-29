package org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState

@Stable
data class AudioInputFormatConfigurationViewState internal constructor(
    override val editData: AudioInputFormatConfigurationData,
) : IConfigurationViewState {

    @Stable
    data class AudioInputFormatConfigurationData internal constructor(
        val audioInputChannel: AudioFormatChannelType,
        val audioInputEncoding: AudioFormatEncodingType,
        val audioInputSampleRate: AudioFormatSampleRateType,
    ) {

        val audioFormatChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.entries.toTypedArray().toImmutableList()
        val audioFormatEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
        val audioFormatSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.entries.toTypedArray().toImmutableList()

    }

    //TODO info only one encoding working for porcupine or disable?
}