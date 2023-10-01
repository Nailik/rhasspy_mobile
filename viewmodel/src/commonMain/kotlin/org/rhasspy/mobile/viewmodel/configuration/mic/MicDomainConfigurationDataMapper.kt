package org.rhasspy.mobile.viewmodel.configuration.mic

import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.viewmodel.configuration.mic.MicDomainConfigurationViewState.MicDomainConfigurationData

class MicDomainConfigurationDataMapper {

    operator fun invoke(data: MicDomainData): MicDomainConfigurationData {
        return MicDomainConfigurationData(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
            audioOutputChannel = data.audioOutputChannel,
            audioOutputEncoding = data.audioOutputEncoding,
            audioOutputSampleRate = data.audioOutputSampleRate,
            isUseLoudnessEnhancer = data.isUseLoudnessEnhancer,
            gainControl = data.gainControl,
            isPauseRecordingOnMediaPlayback = data.isPauseRecordingOnMediaPlayback,
        )
    }

    operator fun invoke(data: MicDomainConfigurationData): MicDomainData {
        return MicDomainData(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
            audioOutputChannel = data.audioOutputChannel,
            audioOutputEncoding = data.audioOutputEncoding,
            audioOutputSampleRate = data.audioOutputSampleRate,
            isUseLoudnessEnhancer = data.isUseLoudnessEnhancer,
            gainControl = data.gainControl,
            isPauseRecordingOnMediaPlayback = data.isPauseRecordingOnMediaPlayback,
        )
    }

}