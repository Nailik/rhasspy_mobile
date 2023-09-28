package org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection

import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionViewState.VoiceActivityDetectionConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection.VoiceActivityDetectionViewState.VoiceActivityDetectionConfigurationData.LocalSilenceDetectionConfigurationData

class VoiceActivityDetectionConfigurationDataMapper {

    operator fun invoke(data: VadDomainData): VoiceActivityDetectionConfigurationData {
        return VoiceActivityDetectionConfigurationData(
            voiceActivityDetectionOption = data.option,
            timeout = data.timeout,
            localSilenceDetectionSetting = LocalSilenceDetectionConfigurationData(
                silenceDetectionTime = data.automaticSilenceDetectionTime,
                silenceDetectionMinimumTime = data.automaticSilenceDetectionMinimumTime,
                silenceDetectionAudioLevel = data.automaticSilenceDetectionAudioLevel,
            ),
        )
    }

    operator fun invoke(data: VoiceActivityDetectionConfigurationData): VadDomainData {
        return VadDomainData(
            option = data.voiceActivityDetectionOption,
            timeout = data.timeout,
            automaticSilenceDetectionTime = data.localSilenceDetectionSetting.silenceDetectionTime,
            automaticSilenceDetectionMinimumTime = data.localSilenceDetectionSetting.silenceDetectionMinimumTime,
            automaticSilenceDetectionAudioLevel = data.localSilenceDetectionSetting.silenceDetectionAudioLevel,
        )
    }

}
