package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import org.rhasspy.mobile.data.service.option.IntentDomainOption

sealed interface IntentRecognitionConfigurationUiEvent {

    sealed interface Change : IntentRecognitionConfigurationUiEvent {

        data class SelectIntentRecognitionOption(val option: IntentDomainOption) : Change

    }

    sealed interface Action : IntentRecognitionConfigurationUiEvent {

        data object BackClick : Action

    }

}