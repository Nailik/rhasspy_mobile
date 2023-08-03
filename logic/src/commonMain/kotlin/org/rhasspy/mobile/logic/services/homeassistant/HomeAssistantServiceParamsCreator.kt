package org.rhasspy.mobile.logic.services.homeassistant

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class HomeAssistantServiceParamsCreator {

    operator fun invoke(): StateFlow<HomeAssistantServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.siteId.data,
            ConfigurationSetting.intentHandlingHomeAssistantOption.data
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): HomeAssistantServiceParams {
        return HomeAssistantServiceParams(
            siteId = ConfigurationSetting.siteId.value,
            intentHandlingHomeAssistantOption = ConfigurationSetting.intentHandlingHomeAssistantOption.value
        )
    }

}