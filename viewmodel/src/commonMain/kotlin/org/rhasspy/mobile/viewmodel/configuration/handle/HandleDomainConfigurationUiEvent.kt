package org.rhasspy.mobile.viewmodel.configuration.handle

import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption

sealed interface HandleDomainConfigurationUiEvent {

    sealed interface Change : HandleDomainConfigurationUiEvent {
        data class SelectHandleDomainOption(val option: HandleDomainOption) : Change
        data class SelectHandleDomainHomeAssistantOption(val option: HomeAssistantIntentHandlingOption) : Change
        data class UpdateHandleDomainHomeAssistantEventTimeout(val timeout: String) : Change

    }


}