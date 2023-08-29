package org.rhasspy.mobile.logic.domains.texttospeech

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class TextToSpeechServiceParamsCreator {

    operator fun invoke(): StateFlow<TextToSpeechServiceParams> {
        return combineStateFlow(
            ConfigurationSetting.siteId.data,
            ConfigurationSetting.textToSpeechOption.data
        ).mapReadonlyState {
            getParams()
        }
    }

    private fun getParams(): TextToSpeechServiceParams {
        return TextToSpeechServiceParams(
            siteId = ConfigurationSetting.siteId.value,
            textToSpeechOption = ConfigurationSetting.textToSpeechOption.value
        )
    }

}