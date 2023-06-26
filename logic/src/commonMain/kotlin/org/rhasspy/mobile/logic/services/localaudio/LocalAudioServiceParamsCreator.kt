package org.rhasspy.mobile.logic.services.localaudio

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

class LocalAudioServiceParamsCreator {

    operator fun invoke(): StateFlow<LocalAudioServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.audioOutputOption.data
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): LocalAudioServiceParams {
        return LocalAudioServiceParams(
            audioOutputOption = ConfigurationSetting.audioOutputOption.value
        )
    }

}