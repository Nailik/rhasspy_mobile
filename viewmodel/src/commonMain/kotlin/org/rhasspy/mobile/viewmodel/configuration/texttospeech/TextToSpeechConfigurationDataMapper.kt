package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import org.rhasspy.mobile.data.domain.TtsDomainData
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationViewState.TextToSpeechConfigurationData

class TextToSpeechConfigurationDataMapper {

    operator fun invoke(data: TtsDomainData): TextToSpeechConfigurationData {
        return TextToSpeechConfigurationData(
            textToSpeechOption = data.option,
            rhasspy2HermesMqttTimeout = data.rhasspy2HermesMqttTimeout,
        )
    }

    operator fun invoke(data: TextToSpeechConfigurationData): TtsDomainData {
        return TtsDomainData(
            option = data.textToSpeechOption,
            rhasspy2HermesMqttTimeout = data.rhasspy2HermesMqttTimeout,
        )
    }

}