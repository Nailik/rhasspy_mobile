package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import org.rhasspy.mobile.data.service.option.HandleDomainOption
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption

sealed interface IntentHandlingConfigurationUiEvent {

    sealed interface Change : IntentHandlingConfigurationUiEvent {
        data class SelectIntentHandlingOption(val option: HandleDomainOption) : Change
        data class SelectIntentHandlingHomeAssistantOption(val option: HomeAssistantIntentHandlingOption) : Change

    }

    sealed interface Action : IntentHandlingConfigurationUiEvent {

        data object BackClick : Action

    }


}