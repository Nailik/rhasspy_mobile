package org.rhasspy.mobile.viewmodel.configuration.domains.mic

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.audiorecorder.AudioSourceType
import org.rhasspy.mobile.data.domain.DomainState
import org.rhasspy.mobile.logic.domains.mic.MicDomainState

@Stable
data class MicDomainConfigurationViewState internal constructor(
    val editData: MicDomainConfigurationData,
    val micDomainStateFlow: StateFlow<MicDomainState>,
    val domainStateFlow: StateFlow<DomainState>,
    val isPauseRecordingOnMediaPlaybackEnabled: Boolean,
) {

    @Stable
    data class MicDomainConfigurationData internal constructor(
        val audioInputSource: AudioSourceType,
        val audioInputChannel: AudioFormatChannelType,
        val audioInputEncoding: AudioFormatEncodingType,
        val audioInputSampleRate: AudioFormatSampleRateType,
        val audioOutputChannel: AudioFormatChannelType,
        val audioOutputEncoding: AudioFormatEncodingType,
        val audioOutputSampleRate: AudioFormatSampleRateType,
        val isPauseRecordingOnMediaPlayback: Boolean,
    )

}
