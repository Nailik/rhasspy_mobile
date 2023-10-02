package org.rhasspy.mobile.viewmodel.configuration.asr

import org.rhasspy.mobile.data.service.option.AsrDomainOption

sealed interface AsrConfigurationUiEvent {

    sealed interface Change : AsrConfigurationUiEvent {

        data class SelectAsrOption(val option: AsrDomainOption) : Change
        data class SetUseAsrMqttSilenceDetection(val enabled: Boolean) : Change
        data class UpdateMqttResultTimeout(val timeout: String) : Change
        data class UpdateVoiceTimeout(val timeout: String) : Change

    }

}