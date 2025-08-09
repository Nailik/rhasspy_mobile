package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class AudioPlayingConfigurationViewState(
    override val editData: AudioPlayingConfigurationData
) : IConfigurationViewState {

    @Stable
    data class AudioPlayingConfigurationData(
        val audioPlayingOption: AudioPlayingOption = ConfigurationSetting.audioPlayingOption.value,
        val audioOutputOption: AudioOutputOption = ConfigurationSetting.audioOutputOption.value,
        val isUseCustomAudioPlayingHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value,
        val audioPlayingHttpEndpoint: String = ConfigurationSetting.audioPlayingHttpEndpoint.value,
        val audioPlayingMqttSiteId: String = ConfigurationSetting.audioPlayingMqttSiteId.value
    ) : IConfigurationData {

        val audioPlayingOptionList: ImmutableList<AudioPlayingOption> =
            AudioPlayingOption.entries.toImmutableList()
        val audioOutputOptionList: ImmutableList<AudioOutputOption> =
            AudioOutputOption.entries.toImmutableList()

    }

}