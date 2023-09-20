package org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat

import org.rhasspy.mobile.data.domain.MicDomainData
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.audioinput.audiooutputformat.AudioOutputFormatConfigurationViewState.AudioOutputFormatConfigurationData

class AudioOutputFormatConfigurationDataMapper {

    operator fun invoke(data: MicDomainData): AudioOutputFormatConfigurationData {
        return AudioOutputFormatConfigurationData(
            audioOutputChannel = data.audioInputChannel,
            audioOutputEncoding = data.audioInputEncoding,
            audioOutputSampleRate = data.audioInputSampleRate,
        )
    }

    operator fun invoke(data: AudioOutputFormatConfigurationData): MicDomainData {
        return ConfigurationSetting.micDomainData.value.copy(
            audioOutputChannel = data.audioOutputChannel,
            audioOutputEncoding = data.audioOutputEncoding,
            audioOutputSampleRate = data.audioOutputSampleRate,
        )
    }

}