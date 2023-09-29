package org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.connections.IConfigurationViewState.IConfigurationData
import kotlin.time.Duration

@Stable
data class VoiceActivityDetectionViewState internal constructor(
    override val editData: VoiceActivityDetectionConfigurationData,
) : IConfigurationViewState {

    @Stable
    data class VoiceActivityDetectionConfigurationData internal constructor(
        val voiceActivityDetectionOption: VoiceActivityDetectionOption,
        val localSilenceDetectionSetting: LocalSilenceDetectionConfigurationData,
        val timeout: Duration,
    ) : IConfigurationData {

        val voiceActivityDetectionOptions: ImmutableList<VoiceActivityDetectionOption> = VoiceActivityDetectionOption.entries.toTypedArray().toImmutableList()

        @Stable
        data class LocalSilenceDetectionConfigurationData internal constructor(
            val silenceDetectionTime: Duration,
            val silenceDetectionMinimumTime: Duration,
            val silenceDetectionAudioLevel: Float,
        ) {

            val silenceDetectionTimeText: String = silenceDetectionTime.inWholeMilliseconds.toString()
            val silenceDetectionMinimumTimeText: String = silenceDetectionMinimumTime.inWholeMilliseconds.toString()

        }

    }

}