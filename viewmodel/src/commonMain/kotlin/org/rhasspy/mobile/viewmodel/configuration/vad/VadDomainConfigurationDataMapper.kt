package org.rhasspy.mobile.viewmodel.configuration.vad

import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainViewState.VadDomainConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainViewState.VadDomainConfigurationData.LocalSilenceDetectionConfigurationData

class VadDomainConfigurationDataMapper {

    operator fun invoke(data: VadDomainData): VadDomainConfigurationData {
        return VadDomainConfigurationData(
            voiceActivityDetectionOption = data.option,
            timeout = data.timeout,
            localSilenceDetectionSetting = LocalSilenceDetectionConfigurationData(
                silenceDetectionTime = data.automaticSilenceDetectionTime,
                silenceDetectionMinimumTime = data.automaticSilenceDetectionMinimumTime,
                silenceDetectionAudioLevel = data.automaticSilenceDetectionAudioLevel,
            ),
        )
    }

    operator fun invoke(data: VadDomainConfigurationData): VadDomainData {
        return VadDomainData(
            option = data.voiceActivityDetectionOption,
            timeout = data.timeout,
            automaticSilenceDetectionTime = data.localSilenceDetectionSetting.silenceDetectionTime,
            automaticSilenceDetectionMinimumTime = data.localSilenceDetectionSetting.silenceDetectionMinimumTime,
            automaticSilenceDetectionAudioLevel = data.localSilenceDetectionSetting.silenceDetectionAudioLevel,
        )
    }

}
