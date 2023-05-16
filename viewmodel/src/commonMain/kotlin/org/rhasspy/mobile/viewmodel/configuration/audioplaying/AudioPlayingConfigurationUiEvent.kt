package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption

sealed interface AudioPlayingConfigurationUiEvent {

    sealed interface Change : AudioPlayingConfigurationUiEvent {
        data class SelectAudioPlayingOption(val option: AudioPlayingOption) : Change
        data class SelectAudioOutputOption(val option: AudioOutputOption) : Change
        data class SetUseCustomHttpEndpoint(val enabled: Boolean) : Change
        data class ChangeAudioPlayingHttpEndpoint(val enabled: String) : Change
        data class ChangeAudioPlayingMqttSiteId(val siteId: String) : Change

    }

    sealed interface Action : AudioPlayingConfigurationUiEvent {

        object PlayTestAudio : Action
        object BackClick : Action

    }

}