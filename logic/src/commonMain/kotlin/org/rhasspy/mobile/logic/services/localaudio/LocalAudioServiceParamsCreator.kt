package org.rhasspy.mobile.logic.services.localaudio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class LocalAudioServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<LocalAudioServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.audioOutputOption.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): LocalAudioServiceParams {
        return LocalAudioServiceParams(
            audioOutputOption = ConfigurationSetting.audioOutputOption.value
        )
    }

}