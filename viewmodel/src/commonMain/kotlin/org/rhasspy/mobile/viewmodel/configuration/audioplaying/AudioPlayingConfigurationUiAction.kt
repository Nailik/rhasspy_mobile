package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption

sealed interface AudioPlayingConfigurationUiAction {

    data class SelectAudioPlayingOption(val option: AudioPlayingOption): AudioPlayingConfigurationUiAction
    data class SelectAudioOutputOption(val option: AudioOutputOption): AudioPlayingConfigurationUiAction
    object ToggleUseCustomHttpEndpoint: AudioPlayingConfigurationUiAction
    data class ChangeAudioPlayingHttpEndpoint(val value: String): AudioPlayingConfigurationUiAction
    data class ChangeAudioPlayingMqttSiteId(val value: String): AudioPlayingConfigurationUiAction

}