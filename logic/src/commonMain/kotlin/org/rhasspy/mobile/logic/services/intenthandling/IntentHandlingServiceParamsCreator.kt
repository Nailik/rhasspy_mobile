package org.rhasspy.mobile.logic.services.intenthandling

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class IntentHandlingServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<IntentHandlingServiceParams> {

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.intentHandlingOption.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): IntentHandlingServiceParams {
        return IntentHandlingServiceParams(
            intentHandlingOption = ConfigurationSetting.intentHandlingOption.value
        )
    }
}