package org.rhasspy.mobile.logic.domains.intentrecognition

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class IntentRecognitionServiceParamsCreator {

    operator fun invoke(): StateFlow<IntentRecognitionServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.intentRecognitionOption.data
        ).mapReadonlyState {
            getParams()
        }

    }


    private fun getParams(): IntentRecognitionServiceParams {
        return IntentRecognitionServiceParams(
            intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value
        )
    }
}