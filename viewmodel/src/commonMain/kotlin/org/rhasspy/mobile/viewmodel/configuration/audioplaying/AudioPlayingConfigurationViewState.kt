package org.rhasspy.mobile.viewmodel.configuration.audioplaying

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import kotlin.time.Duration

@Stable
data class AudioPlayingConfigurationViewState internal constructor(
    val editData: AudioPlayingConfigurationData
) {

    @Stable
    data class AudioPlayingConfigurationData internal constructor(
        val audioPlayingOption: AudioPlayingOption,
        val audioOutputOption: AudioOutputOption,
        val audioPlayingMqttSiteId: String,
        val audioTimeout: Duration,
        val rhasspy2HermesMqttTimeout: Duration,
    ) {

        val audioPlayingOptionList: ImmutableList<AudioPlayingOption> = AudioPlayingOption.entries.toImmutableList()
        val audioOutputOptionList: ImmutableList<AudioOutputOption> = AudioOutputOption.entries.toImmutableList()

    }

}