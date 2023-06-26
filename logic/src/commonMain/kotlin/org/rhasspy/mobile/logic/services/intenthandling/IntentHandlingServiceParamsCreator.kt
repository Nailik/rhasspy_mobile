package org.rhasspy.mobile.logic.services.intenthandling

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

class IntentHandlingServiceParamsCreator {

    operator fun invoke(): StateFlow<IntentHandlingServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.intentHandlingOption.data
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): IntentHandlingServiceParams {
        return IntentHandlingServiceParams(
            intentHandlingOption = ConfigurationSetting.intentHandlingOption.value
        )
    }
}