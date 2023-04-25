package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class WakeWordServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<WakeWordServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.wakeWordOption.data,
                ConfigurationSetting.wakeWordPorcupineAccessToken.data,
                ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.data,
                ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.data,
                ConfigurationSetting.wakeWordPorcupineLanguage.data,
                ConfigurationSetting.wakeWordUdpOutputHost.data,
                ConfigurationSetting.wakeWordUdpOutputPort.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): WakeWordServiceParams {
        return WakeWordServiceParams(
            wakeWordOption = ConfigurationSetting.wakeWordOption.value,
            wakeWordPorcupineAccessToken = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
            wakeWordPorcupineKeywordDefaultOptions = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
            wakeWordPorcupineKeywordCustomOptions = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
            wakeWordPorcupineLanguage = ConfigurationSetting.wakeWordPorcupineLanguage.value,
            wakeWordUdpOutputHost = ConfigurationSetting.wakeWordUdpOutputHost.value,
            wakeWordUdpOutputPort = ConfigurationSetting.wakeWordUdpOutputPort.value
        )
    }

}