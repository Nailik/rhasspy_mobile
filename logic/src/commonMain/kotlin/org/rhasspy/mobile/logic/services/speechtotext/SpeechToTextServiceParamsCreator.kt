package org.rhasspy.mobile.logic.services.speechtotext

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class SpeechToTextServiceParamsCreator {

    operator fun invoke(): StateFlow<SpeechToTextServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.speechToTextOption.data,
            ConfigurationSetting.dialogManagementOption.data,
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): SpeechToTextServiceParams {
        return SpeechToTextServiceParams(
            speechToTextOption = ConfigurationSetting.speechToTextOption.value,
            dialogManagementOption = ConfigurationSetting.dialogManagementOption.value
        )
    }

}