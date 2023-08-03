package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import org.rhasspy.mobile.data.service.option.IntentRecognitionOption

sealed interface IntentRecognitionConfigurationUiEvent {

    sealed interface Change : IntentRecognitionConfigurationUiEvent {

        data class SelectIntentRecognitionOption(val option: IntentRecognitionOption) : Change
        data class SetUseCustomHttpEndpoint(val enabled: Boolean) : Change
        data class ChangeIntentRecognitionHttpEndpoint(val endpoint: String) : Change

    }

    sealed interface Action : IntentRecognitionConfigurationUiEvent {

        data object BackClick : Action

    }

}