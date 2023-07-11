package org.rhasspy.mobile.logic.services.audioplaying

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class AudioPlayingServiceParamsCreator {

    operator fun invoke(): StateFlow<AudioPlayingServiceParams> {
        return combineStateFlow(
            ConfigurationSetting.audioPlayingOption.data
        ).mapReadonlyState {
            getParams()
        }
    }

    private fun getParams(): AudioPlayingServiceParams {
        return AudioPlayingServiceParams(
            audioPlayingOption = ConfigurationSetting.audioPlayingOption.value
        )
    }

}