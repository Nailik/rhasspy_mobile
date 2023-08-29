package org.rhasspy.mobile.logic.domains.voiceactivitydetection

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class VoiceActivityDetectionParamsCreator {

    operator fun invoke(): StateFlow<VoiceActivityDetectionParams> {

        return combineStateFlow(
            ConfigurationSetting.voiceActivityDetectionOption.data,
            ConfigurationSetting.automaticSilenceDetectionAudioLevel.data,
            ConfigurationSetting.automaticSilenceDetectionTime.data,
            ConfigurationSetting.automaticSilenceDetectionMinimumTime.data,
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): VoiceActivityDetectionParams {
        return VoiceActivityDetectionParams(
            voiceActivityDetectionOption = ConfigurationSetting.voiceActivityDetectionOption.value,
            automaticSilenceDetectionAudioLevel = ConfigurationSetting.automaticSilenceDetectionAudioLevel.value,
            automaticSilenceDetectionTime = ConfigurationSetting.automaticSilenceDetectionTime.value,
            automaticSilenceDetectionMinimumTime = ConfigurationSetting.automaticSilenceDetectionMinimumTime.value,
        )
    }

}