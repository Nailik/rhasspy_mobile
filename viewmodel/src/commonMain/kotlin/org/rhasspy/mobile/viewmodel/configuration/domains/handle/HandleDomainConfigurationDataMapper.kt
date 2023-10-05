package org.rhasspy.mobile.viewmodel.configuration.domains.handle

import org.rhasspy.mobile.data.data.toIntOrZero
import org.rhasspy.mobile.data.domain.HandleDomainData
import org.rhasspy.mobile.viewmodel.configuration.domains.handle.HandleDomainConfigurationViewState.HandleDomainConfigurationData
import kotlin.time.Duration.Companion.seconds

class HandleDomainConfigurationDataMapper {

    operator fun invoke(data: HandleDomainData): HandleDomainConfigurationData {
        return HandleDomainConfigurationData(
            handleDomainOption = data.option,
            intentHandlingHomeAssistantOption = data.homeAssistantIntentHandlingOption,
            homeAssistantEventTimeout = data.homeAssistantEventTimeout.inWholeSeconds.toString(),
        )
    }

    operator fun invoke(data: HandleDomainConfigurationData): HandleDomainData {
        return HandleDomainData(
            option = data.handleDomainOption,
            homeAssistantIntentHandlingOption = data.intentHandlingHomeAssistantOption,
            homeAssistantEventTimeout = data.homeAssistantEventTimeout.toIntOrZero().seconds,
        )
    }

}