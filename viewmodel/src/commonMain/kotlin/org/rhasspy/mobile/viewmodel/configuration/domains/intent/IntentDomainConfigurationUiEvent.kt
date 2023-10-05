package org.rhasspy.mobile.viewmodel.configuration.domains.intent

import org.rhasspy.mobile.data.service.option.IntentDomainOption

sealed interface IntentDomainConfigurationUiEvent {

    sealed interface Change : IntentDomainConfigurationUiEvent {

        data class SelectIntentDomainOption(val option: IntentDomainOption) : Change

        data class SetRhasspy2HttpIntentIntentHandlingEnabled(val enabled: Boolean) : Change

        data class UpdateRhasspy2HttpIntentHandlingTimeout(val timeout: String) : Change

        data class UpdateVoiceTimeout(val timeout: String) : Change

    }

}