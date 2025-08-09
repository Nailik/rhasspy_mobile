package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption

sealed interface IntentHandlingConfigurationUiEvent {

    sealed interface Change : IntentHandlingConfigurationUiEvent {
        data class SelectIntentHandlingOption(val option: IntentHandlingOption) : Change
        data class ChangeIntentHandlingHttpEndpoint(val endpoint: String) : Change
        data class ChangeIntentHandlingHomeAssistantEndpoint(val endpoint: String) : Change
        data class ChangeIntentHandlingHomeAssistantAccessToken(val token: String) : Change
        data class SelectIntentHandlingHomeAssistantOption(val option: HomeAssistantIntentHandlingOption) :
            Change

    }

    sealed interface Action : IntentHandlingConfigurationUiEvent {

        data object BackClick : Action
        data object ScanHomeAssistantAccessToken : Action

    }


}