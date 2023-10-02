package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import org.rhasspy.mobile.data.domain.HandleDomainData
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewState.IntentHandlingConfigurationData

class IntentHandlingConfigurationDataMapper {

    operator fun invoke(data: HandleDomainData): IntentHandlingConfigurationData {
        return IntentHandlingConfigurationData(
            handleDomainOption = data.option,
            intentHandlingHomeAssistantOption = data.homeAssistantIntentHandlingOption,
            homeAssistantEventTimeout = data.homeAssistantEventTimeout,
        )
    }

    operator fun invoke(data: IntentHandlingConfigurationData): HandleDomainData {
        return HandleDomainData(
            option = data.handleDomainOption,
            homeAssistantIntentHandlingOption = data.intentHandlingHomeAssistantOption,
            homeAssistantEventTimeout = data.homeAssistantEventTimeout,
        )
    }

}