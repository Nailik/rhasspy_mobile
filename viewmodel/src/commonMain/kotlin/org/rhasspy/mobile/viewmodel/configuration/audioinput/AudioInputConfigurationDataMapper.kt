package org.rhasspy.mobile.viewmodel.configuration.audioinput

import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationViewState.AudioInputConfigurationData

class AudioInputConfigurationDataMapper {

    operator fun invoke(data: MicDomainData): AudioInputConfigurationData {
        return AudioInputConfigurationData(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
            audioOutputChannel = data.audioOutputChannel,
            audioOutputEncoding = data.audioOutputEncoding,
            audioOutputSampleRate = data.audioOutputSampleRate,
            isUseAutomaticGainControl = data.isUseAutomaticGainControl,
            isPauseRecordingOnMediaPlayback = data.isPauseRecordingOnMediaPlayback,
        )
    }

    operator fun invoke(data: AudioInputConfigurationData): MicDomainData {
        return MicDomainData(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
            audioOutputChannel = data.audioOutputChannel,
            audioOutputEncoding = data.audioOutputEncoding,
            audioOutputSampleRate = data.audioOutputSampleRate,
            isUseAutomaticGainControl = data.isUseAutomaticGainControl,
            isPauseRecordingOnMediaPlayback = data.isPauseRecordingOnMediaPlayback,
        )
    }

}