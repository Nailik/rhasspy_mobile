package org.rhasspy.mobile.viewmodel.configuration.audioinput

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType

@Stable
data class AudioInputConfigurationViewState internal constructor(
    val editData: AudioInputConfigurationData,
    val isUseAutomaticGainControlVisible: Boolean,
) {

    @Stable
    data class AudioInputConfigurationData internal constructor(
        val audioInputChannel: AudioFormatChannelType,
        val audioInputEncoding: AudioFormatEncodingType,
        val audioInputSampleRate: AudioFormatSampleRateType,
        val audioOutputChannel: AudioFormatChannelType,
        val audioOutputEncoding: AudioFormatEncodingType,
        val audioOutputSampleRate: AudioFormatSampleRateType,
        val isUseAutomaticGainControl: Boolean,
        val isPauseRecordingOnMediaPlayback: Boolean,
    )

}
