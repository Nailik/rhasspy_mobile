package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import org.rhasspy.mobile.data.domain.AsrDomainData
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData

class SpeechToTextConfigurationDataMapper {

    operator fun invoke(data: AsrDomainData): SpeechToTextConfigurationData {
        return SpeechToTextConfigurationData(
            speechToTextOption = data.option,
            isUseSpeechToTextMqttSilenceDetection = data.isUseSpeechToTextMqttSilenceDetection,
        )
    }

    operator fun invoke(data: SpeechToTextConfigurationData): AsrDomainData {
        return AsrDomainData(
            option = data.speechToTextOption,
            isUseSpeechToTextMqttSilenceDetection = data.isUseSpeechToTextMqttSilenceDetection,
        )
    }

}