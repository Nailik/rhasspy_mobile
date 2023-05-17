package org.rhasspy.mobile.logic.services.intentrecognition

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class IntentRecognitionServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<IntentRecognitionServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.intentRecognitionOption.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }


    private fun getParams(): IntentRecognitionServiceParams {
        return IntentRecognitionServiceParams(
            intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value
        )
    }
}