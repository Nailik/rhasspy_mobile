package org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.platformspecific.toStringOrEmpty
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class VoiceActivityDetectionViewState internal constructor(
    override val editData: VoiceActivityDetectionConfigurationData
) : IConfigurationViewState {

    @Stable
    data class VoiceActivityDetectionConfigurationData internal constructor(
        val voiceActivityDetectionOption: VoiceActivityDetectionOption = ConfigurationSetting.voiceActivityDetectionOption.value,
        val localSilenceDetectionSetting: LocalSilenceDetectionConfigurationData = LocalSilenceDetectionConfigurationData(),
    ) : IConfigurationData {

        val voiceActivityDetectionOptions: ImmutableList<VoiceActivityDetectionOption> = VoiceActivityDetectionOption.entries.toTypedArray().toImmutableList()

        @Stable
        data class LocalSilenceDetectionConfigurationData internal constructor(
            val silenceDetectionTime: Long? = ConfigurationSetting.automaticSilenceDetectionTime.value,
            val silenceDetectionMinimumTime: Long? = ConfigurationSetting.automaticSilenceDetectionMinimumTime.value,
            val silenceDetectionAudioLevel: Float = ConfigurationSetting.automaticSilenceDetectionAudioLevel.value,
        ) {

            val silenceDetectionTimeText: String = silenceDetectionTime.toStringOrEmpty()
            val silenceDetectionMinimumTimeText: String = silenceDetectionMinimumTime.toStringOrEmpty()

        }

    }

}