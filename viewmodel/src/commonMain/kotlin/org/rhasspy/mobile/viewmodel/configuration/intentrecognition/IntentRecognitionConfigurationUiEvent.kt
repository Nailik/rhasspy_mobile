package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import org.rhasspy.mobile.data.service.option.IntentDomainOption

sealed interface IntentRecognitionConfigurationUiEvent {

    sealed interface Change : IntentRecognitionConfigurationUiEvent {

        data class SelectIntentRecognitionOption(val option: IntentDomainOption) : Change

        data class SetRhasspy2HttpIntentIntentHandlingEnabled(val enabled: Boolean) : Change

        data class UpdateRhasspy2HttpIntentHandlingTimeout(val timeout: String) : Change

        data class UpdateVoiceTimeout(val timeout: String) : Change

    }

}