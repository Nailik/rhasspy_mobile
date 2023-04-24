package org.rhasspy.mobile.logic.services.speechtotext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow

class SpeechToTextServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<SpeechToTextServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.speechToTextOption.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): SpeechToTextServiceParams {
        return SpeechToTextServiceParams(
            speechToTextOption = ConfigurationSetting.speechToTextOption.value
        )
    }

}