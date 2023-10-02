package org.rhasspy.mobile.viewmodel.configuration.asr

import org.rhasspy.mobile.data.domain.AsrDomainData
import org.rhasspy.mobile.viewmodel.configuration.asr.AsrConfigurationViewState.AsrConfigurationData

class AsrConfigurationDataMapper {

    operator fun invoke(data: AsrDomainData): AsrConfigurationData {
        return AsrConfigurationData(
            asrDomainOption = data.option,
            isUseSpeechToTextMqttSilenceDetection = data.isUseSpeechToTextMqttSilenceDetection,
            voiceTimeout = data.voiceTimeout,
            mqttResultTimeout = data.mqttResultTimeout,
        )
    }

    operator fun invoke(data: AsrConfigurationData): AsrDomainData {
        return AsrDomainData(
            option = data.asrDomainOption,
            isUseSpeechToTextMqttSilenceDetection = data.isUseSpeechToTextMqttSilenceDetection,
            voiceTimeout = data.voiceTimeout,
            mqttResultTimeout = data.mqttResultTimeout,
        )
    }

}