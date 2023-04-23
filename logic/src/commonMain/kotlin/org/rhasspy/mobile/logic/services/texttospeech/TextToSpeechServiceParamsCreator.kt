package org.rhasspy.mobile.logic.services.texttospeech

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow

class TextToSpeechServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<TextToSpeechServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.textToSpeechOption.data
            ).onEach {
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