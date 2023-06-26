package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

class WakeWordServiceParamsCreator {

    operator fun invoke(): StateFlow<WakeWordServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.wakeWordOption.data,
            ConfigurationSetting.wakeWordPorcupineAccessToken.data,
            ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.data,
            ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.data,
            ConfigurationSetting.wakeWordPorcupineLanguage.data,
            ConfigurationSetting.wakeWordUdpOutputHost.data,
            ConfigurationSetting.wakeWordUdpOutputPort.data
        ).mapReadonlyState {
            getParams()
        }

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