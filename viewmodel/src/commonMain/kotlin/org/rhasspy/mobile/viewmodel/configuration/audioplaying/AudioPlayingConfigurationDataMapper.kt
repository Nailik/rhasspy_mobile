package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import org.rhasspy.mobile.data.domain.SndDomainData
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewState.AudioPlayingConfigurationData

class AudioPlayingConfigurationDataMapper {

    operator fun invoke(data: SndDomainData): AudioPlayingConfigurationData {
        return AudioPlayingConfigurationData(
            audioPlayingOption = data.option,
            audioOutputOption = data.localOutputOption,
            audioPlayingMqttSiteId = data.mqttSiteId
        )
    }

    operator fun invoke(data: AudioPlayingConfigurationData): SndDomainData {
        return SndDomainData(
            option = data.audioPlayingOption,
            localOutputOption = data.audioOutputOption,
            mqttSiteId = data.audioPlayingMqttSiteId
        )
    }

}