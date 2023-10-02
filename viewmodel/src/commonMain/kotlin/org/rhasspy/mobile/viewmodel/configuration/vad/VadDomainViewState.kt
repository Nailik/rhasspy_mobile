package org.rhasspy.mobile.viewmodel.configuration.vad

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import kotlin.time.Duration

@Stable
data class VadDomainViewState internal constructor(
    val editData: VadDomainConfigurationData,
) {

    @Stable
    data class VadDomainConfigurationData internal constructor(
        val voiceActivityDetectionOption: VoiceActivityDetectionOption,
        val localSilenceDetectionSetting: LocalSilenceDetectionConfigurationData,
        val timeout: Duration,
    ) {

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