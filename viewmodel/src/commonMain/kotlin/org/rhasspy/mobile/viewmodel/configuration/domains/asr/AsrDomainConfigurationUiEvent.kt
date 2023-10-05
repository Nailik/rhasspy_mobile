package org.rhasspy.mobile.viewmodel.configuration.domains.asr

import org.rhasspy.mobile.data.service.option.AsrDomainOption

sealed interface AsrDomainConfigurationUiEvent {

    sealed interface Change : AsrDomainConfigurationUiEvent {

        data class SelectAsrOptionDomain(val option: AsrDomainOption) : Change
        data class SetUseAsrMqttSilenceDetectionDomain(val enabled: Boolean) : Change
        data class UpdateMqttResultTimeout(val timeout: String) : Change
        data class UpdateVoiceTimeout(val timeout: String) : Change

    }

}