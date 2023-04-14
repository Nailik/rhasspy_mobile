package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption

sealed interface IntentHandlingConfigurationUiAction {

    data class SelectIntentHandlingOption(val option: IntentHandlingOption) : IntentHandlingConfigurationUiAction
    data class ChangeIntentHandlingHttpEndpoint(val value: String) : IntentHandlingConfigurationUiAction
    data class ChangeIntentHandlingHassEndpoint(val value: String) : IntentHandlingConfigurationUiAction
    data class ChangeIntentHandlingHassAccessToken(val value: String) : IntentHandlingConfigurationUiAction
    data class SelectIntentHandlingHassOption(val option: HomeAssistantIntentHandlingOption) : IntentHandlingConfigurationUiAction

}