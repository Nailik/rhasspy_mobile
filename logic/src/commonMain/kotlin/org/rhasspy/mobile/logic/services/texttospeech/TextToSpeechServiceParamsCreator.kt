package org.rhasspy.mobile.logic.services.texttospeech

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class TextToSpeechServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<TextToSpeechServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.textToSpeechOption.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): TextToSpeechServiceParams {
        return TextToSpeechServiceParams(
            textToSpeechOption = ConfigurationSetting.textToSpeechOption.value
        )
    }

}