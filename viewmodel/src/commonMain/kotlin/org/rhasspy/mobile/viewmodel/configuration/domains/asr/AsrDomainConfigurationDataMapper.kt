package org.rhasspy.mobile.viewmodel.configuration.domains.asr

import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.data.domain.AsrDomainData
import org.rhasspy.mobile.viewmodel.configuration.domains.asr.AsrDomainConfigurationViewState.AsrDomainConfigurationData
import kotlin.time.Duration.Companion.seconds

class AsrDomainConfigurationDataMapper {

    operator fun invoke(data: AsrDomainData): AsrDomainConfigurationData {
        return AsrDomainConfigurationData(
            asrDomainOption = data.option,
            isUseSpeechToTextMqttSilenceDetection = data.isUseSpeechToTextMqttSilenceDetection,
            voiceTimeout = data.voiceTimeout.inWholeSeconds.toString(),
            mqttResultTimeout = data.mqttResultTimeout.inWholeSeconds.toString(),
        )
    }

    operator fun invoke(data: AsrDomainConfigurationData): AsrDomainData {
        return AsrDomainData(
            option = data.asrDomainOption,
            isUseSpeechToTextMqttSilenceDetection = data.isUseSpeechToTextMqttSilenceDetection,
            voiceTimeout = data.voiceTimeout.toIntOrZero().seconds,
            mqttResultTimeout = data.mqttResultTimeout.toIntOrZero().seconds,
        )
    }

}