package org.rhasspy.mobile.viewmodel.configuration.domains.mic

import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.MicDomainConfigurationViewState.MicDomainConfigurationData

class MicDomainConfigurationDataMapper {

    operator fun invoke(data: MicDomainData): MicDomainConfigurationData {
        return MicDomainConfigurationData(
            audioInputSource = data.audioInputSource,
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
            audioOutputChannel = data.audioOutputChannel,
            audioOutputEncoding = data.audioOutputEncoding,
            audioOutputSampleRate = data.audioOutputSampleRate,
            isPauseRecordingOnMediaPlayback = data.isPauseRecordingOnMediaPlayback,
        )
    }

    operator fun invoke(data: MicDomainConfigurationData): MicDomainData {
        return MicDomainData(
            audioInputSource = data.audioInputSource,
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
            audioOutputChannel = data.audioOutputChannel,
            audioOutputEncoding = data.audioOutputEncoding,
            audioOutputSampleRate = data.audioOutputSampleRate,
            isPauseRecordingOnMediaPlayback = data.isPauseRecordingOnMediaPlayback,
        )
    }

}