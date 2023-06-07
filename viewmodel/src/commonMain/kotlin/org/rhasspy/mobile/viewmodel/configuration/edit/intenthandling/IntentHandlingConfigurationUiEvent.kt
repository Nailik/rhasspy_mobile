package org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling

import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption

sealed interface IntentHandlingConfigurationUiEvent {

    sealed interface Change : IntentHandlingConfigurationUiEvent {
        data class SelectIntentHandlingOption(val option: IntentHandlingOption) : Change
        data class ChangeIntentHandlingHttpEndpoint(val endpoint: String) : Change
        data class ChangeIntentHandlingHassEndpoint(val endpoint: String) : Change
        data class ChangeIntentHandlingHassAccessToken(val token: String) : Change
        data class SelectIntentHandlingHassOption(val option: HomeAssistantIntentHandlingOption) : Change
        data class UpdateTestIntentHandlingName(val name: String) : Change
        data class UpdateTestIntentHandlingText(val text: String) : Change

    }

    sealed interface Action : IntentHandlingConfigurationUiEvent {

        object RunIntentHandlingTest : Action
        object BackClick : Action

    }


}