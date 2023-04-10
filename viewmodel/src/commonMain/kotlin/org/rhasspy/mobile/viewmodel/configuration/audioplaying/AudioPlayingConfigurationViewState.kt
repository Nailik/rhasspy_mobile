package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationContentViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.ServiceStateHeaderViewState

@Stable
data class AudioPlayingConfigurationViewState(
    val audioPlayingOptionList: ImmutableList<AudioPlayingOption>,
    val audioOutputOptionList: ImmutableList<AudioOutputOption>,
    val audioPlayingOption: AudioPlayingOption,
    val audioOutputOption: AudioOutputOption,
    val isUseCustomAudioPlayingHttpEndpoint: Boolean,
    val audioPlayingHttpEndpoint: String,
    val audioPlayingMqttSiteId: String
) : IConfigurationContentViewState {

    companion object {
        fun getInitial() = AudioPlayingConfigurationViewState(
            audioPlayingOptionList = AudioPlayingOption.values().toList().toImmutableList(),
            audioOutputOptionList = AudioOutputOption.values().toList().toImmutableList(),
            audioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
            audioOutputOption = ConfigurationSetting.audioOutputOption.value,
            isUseCustomAudioPlayingHttpEndpoint = ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value,
            audioPlayingHttpEndpoint = ConfigurationSetting.audioPlayingHttpEndpoint.value,
            audioPlayingMqttSiteId = ConfigurationSetting.audioPlayingMqttSiteId.value
        )
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState(
            hasUnsavedChanges = !(audioPlayingOption == ConfigurationSetting.audioPlayingOption.value &&
                    isUseCustomAudioPlayingHttpEndpoint == ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value &&
                    audioPlayingHttpEndpoint == ConfigurationSetting.audioPlayingHttpEndpoint.value &&
                    audioPlayingMqttSiteId == ConfigurationSetting.audioPlayingMqttSiteId.value),
            isTestingEnabled = audioPlayingOption != AudioPlayingOption.Disabled,
            serviceViewState = serviceViewState
        )
    }

    override fun save() {
        ConfigurationSetting.audioPlayingOption.value = audioPlayingOption
        ConfigurationSetting.audioOutputOption.value = audioOutputOption
        ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value = isUseCustomAudioPlayingHttpEndpoint
        ConfigurationSetting.audioPlayingHttpEndpoint.value = audioPlayingHttpEndpoint
        ConfigurationSetting.audioPlayingMqttSiteId.value = audioPlayingMqttSiteId
    }

}