package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import org.rhasspy.mobile.data.domain.SndDomainData
import org.rhasspy.mobile.viewmodel.configuration.audioplaying.AudioPlayingConfigurationViewState.AudioPlayingConfigurationData

class AudioPlayingConfigurationDataMapper {

    operator fun invoke(data: SndDomainData): AudioPlayingConfigurationData {
        return AudioPlayingConfigurationData(
            audioPlayingOption = data.option,
            audioOutputOption = data.localOutputOption,
            audioPlayingMqttSiteId = data.mqttSiteId,
            audioTimeout = data.audioTimeout,
            rhasspy2HermesMqttTimeout = data.rhasspy2HermesMqttTimeout,
        )
    }

    operator fun invoke(data: AudioPlayingConfigurationData): SndDomainData {
        return SndDomainData(
            option = data.audioPlayingOption,
            localOutputOption = data.audioOutputOption,
            mqttSiteId = data.audioPlayingMqttSiteId,
            audioTimeout = data.audioTimeout,
            rhasspy2HermesMqttTimeout = data.rhasspy2HermesMqttTimeout,
        )
    }

}