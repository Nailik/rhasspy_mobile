package org.rhasspy.mobile.viewmodel.configuration.mic.audioinputformat

import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.platformspecific.features.FeatureAvailability
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.mic.audioinputformat.AudioRecorderFormatConfigurationViewState.AudioInputFormatConfigurationData

class AudioRecorderFormatConfigurationDataMapper {

    operator fun invoke(data: MicDomainData): AudioInputFormatConfigurationData {
        return AudioInputFormatConfigurationData(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
        )
    }

    operator fun invoke(data: AudioInputFormatConfigurationData): MicDomainData {
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