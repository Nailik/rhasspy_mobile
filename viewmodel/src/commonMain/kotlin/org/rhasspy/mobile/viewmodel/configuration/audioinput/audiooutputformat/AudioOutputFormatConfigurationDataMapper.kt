package org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat

import org.rhasspy.mobile.data.domain.AudioInputDomainData
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationViewState.AudioOutputFormatConfigurationData

class AudioOutputFormatConfigurationDataMapper {

    operator fun invoke(data: AudioInputDomainData): AudioOutputFormatConfigurationData {
        return AudioOutputFormatConfigurationData(
            audioOutputChannel = data.audioInputChannel,
            audioOutputEncoding = data.audioInputEncoding,
            audioOutputSampleRate = data.audioInputSampleRate,
        )
    }

    operator fun invoke(data: AudioOutputFormatConfigurationData): AudioInputDomainData {
        return ConfigurationSetting.audioInputDomainData.value.copy(
            audioOutputChannel = data.audioOutputChannel,
            audioOutputEncoding = data.audioOutputEncoding,
            audioOutputSampleRate = data.audioOutputSampleRate,
        )
    }

}