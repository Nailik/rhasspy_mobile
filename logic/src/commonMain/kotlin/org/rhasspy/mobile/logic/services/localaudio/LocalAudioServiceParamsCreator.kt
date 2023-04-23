package org.rhasspy.mobile.logic.services.localaudio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow

class LocalAudioServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<LocalAudioServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.audioOutputOption.data
            ).onEach {
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