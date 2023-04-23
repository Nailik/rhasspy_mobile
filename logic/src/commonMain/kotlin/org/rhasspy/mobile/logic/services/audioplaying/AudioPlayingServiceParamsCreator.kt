package org.rhasspy.mobile.logic.services.audioplaying

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow

class AudioPlayingServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.Default)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<AudioPlayingServiceParams> {

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.audioPlayingOption.data
            ).onEach {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): AudioPlayingServiceParams {
        return AudioPlayingServiceParams(
            audioPlayingOption = ConfigurationSetting.audioPlayingOption.value
        )
    }

}