package org.rhasspy.mobile.logic.services.texttospeech

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class TextToSpeechServiceParamsCreator {

    operator fun invoke(): StateFlow<TextToSpeechServiceParams> {
        return combineStateFlow(
            ConfigurationSetting.textToSpeechOption.data
        ).mapReadonlyState {
            getParams()
        }
    }

    private fun getParams(): TextToSpeechServiceParams {
        return TextToSpeechServiceParams(
            textToSpeechOption = ConfigurationSetting.textToSpeechOption.value
        )
    }

}