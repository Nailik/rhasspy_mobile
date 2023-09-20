package org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat

import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audioinputformat.AudioInputFormatConfigurationViewState.AudioInputFormatConfigurationData

class AudioInputFormatConfigurationDataMapper {

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
        )
    }

}