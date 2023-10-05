package org.rhasspy.mobile.viewmodel.configuration.tts

import org.rhasspy.mobile.data.service.option.TtsDomainOption

sealed interface TtsDomainConfigurationUiEvent {

    sealed interface Change : TtsDomainConfigurationUiEvent {

        data class SelectTtsDomainOption(val option: TtsDomainOption) : Change

        data class UpdateRhasspy2HermesMqttTimeout(val timeout: String) : Change

    }

}