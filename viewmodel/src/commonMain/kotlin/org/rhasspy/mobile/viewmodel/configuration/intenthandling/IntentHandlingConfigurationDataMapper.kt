package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import org.rhasspy.mobile.data.domain.HandleDomainData
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewState.IntentHandlingConfigurationData

class IntentHandlingConfigurationDataMapper {

    operator fun invoke(data: HandleDomainData) : IntentHandlingConfigurationData {
        return IntentHandlingConfigurationData(
            intentHandlingOption = data.option,
            intentHandlingHomeAssistantOption = data.homeAssistantIntentHandlingOption
        )
    }

    operator fun invoke(data: IntentHandlingConfigurationData) : HandleDomainData {
        return HandleDomainData(
            option = data.intentHandlingOption,
            homeAssistantIntentHandlingOption = data.intentHandlingHomeAssistantOption
        )
    }

}