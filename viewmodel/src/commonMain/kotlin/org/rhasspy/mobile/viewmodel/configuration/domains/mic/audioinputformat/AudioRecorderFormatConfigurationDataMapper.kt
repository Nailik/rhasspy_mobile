package org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat

import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.platformspecific.features.FeatureAvailability
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.domains.mic.audioinputformat.AudioRecorderFormatConfigurationViewState.AudioRecorderFormatConfigurationData

class AudioRecorderFormatConfigurationDataMapper {

    operator fun invoke(data: MicDomainData): AudioRecorderFormatConfigurationData {
        return AudioRecorderFormatConfigurationData(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
        )
    }

    operator fun invoke(data: AudioRecorderFormatConfigurationData): MicDomainData {
        return ConfigurationSetting.micDomainData.value.copy(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
            audioOutputEncoding = if (!FeatureAvailability.isAudioEncodingOutputChangeEnabled) {
                data.audioInputEncoding
            } else ConfigurationSetting.micDomainData.value.audioOutputEncoding
        )
    }

}