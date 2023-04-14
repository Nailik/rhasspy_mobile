package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import org.rhasspy.mobile.data.service.option.IntentRecognitionOption

sealed interface IntentRecognitionConfigurationUiAction {

    data class SelectIntentRecognitionOption(val option: IntentRecognitionOption) : IntentRecognitionConfigurationUiAction
    object ToggleUseCustomHttpEndpoint : IntentRecognitionConfigurationUiAction
    data class ChangeIntentRecognitionHttpEndpoint(val value: String) : IntentRecognitionConfigurationUiAction

}