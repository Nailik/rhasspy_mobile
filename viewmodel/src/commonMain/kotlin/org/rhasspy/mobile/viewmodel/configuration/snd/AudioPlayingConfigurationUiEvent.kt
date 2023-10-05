package org.rhasspy.mobile.viewmodel.configuration.snd

import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.SndDomainOption

sealed interface AudioPlayingConfigurationUiEvent {

    sealed interface Change : AudioPlayingConfigurationUiEvent {

        data class SelectEditAudioPlayingOption(val option: SndDomainOption) : Change
        data class SelectAudioOutputOption(val option: AudioOutputOption) : Change
        data class ChangeEditAudioPlayingMqttSiteId(val siteId: String) : Change
        data class UpdateAudioTimeout(val timeout: String) : Change
        data class UpdateRhasspy2HermesMqttTimeout(val timeout: String) : Change

    }

}