package org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat

import org.rhasspy.mobile.data.domain.AudioInputDomainData
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationViewState.AudioInputFormatConfigurationData

class AudioInputFormatConfigurationDataMapper {

    operator fun invoke(data: AudioInputDomainData): AudioInputFormatConfigurationData {
        return AudioInputFormatConfigurationData(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
        )
    }

    operator fun invoke(data: AudioInputFormatConfigurationData): AudioInputDomainData {
        return ConfigurationSetting.audioInputDomainData.value.copy(
            audioInputChannel = data.audioInputChannel,
            audioInputEncoding = data.audioInputEncoding,
            audioInputSampleRate = data.audioInputSampleRate,
        )
    }

}