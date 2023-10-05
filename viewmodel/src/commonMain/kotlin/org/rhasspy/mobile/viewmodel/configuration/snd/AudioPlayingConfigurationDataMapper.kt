package org.rhasspy.mobile.viewmodel.configuration.snd

import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.data.domain.SndDomainData
import org.rhasspy.mobile.viewmodel.configuration.snd.AudioPlayingConfigurationViewState.AudioPlayingConfigurationData
import kotlin.time.Duration.Companion.seconds

class AudioPlayingConfigurationDataMapper {

    operator fun invoke(data: SndDomainData): AudioPlayingConfigurationData {
        return AudioPlayingConfigurationData(
            sndDomainOption = data.option,
            audioOutputOption = data.localOutputOption,
            audioPlayingMqttSiteId = data.mqttSiteId,
            audioTimeout = data.audioTimeout.inWholeSeconds.toString(),
            rhasspy2HermesMqttTimeout = data.rhasspy2HermesMqttTimeout.inWholeSeconds.toString(),
        )
    }

    operator fun invoke(data: AudioPlayingConfigurationData): SndDomainData {
        return SndDomainData(
            option = data.sndDomainOption,
            localOutputOption = data.audioOutputOption,
            mqttSiteId = data.audioPlayingMqttSiteId,
            audioTimeout = data.audioTimeout.toIntOrZero().seconds,
            rhasspy2HermesMqttTimeout = data.rhasspy2HermesMqttTimeout.toIntOrZero().seconds,
        )
    }

}