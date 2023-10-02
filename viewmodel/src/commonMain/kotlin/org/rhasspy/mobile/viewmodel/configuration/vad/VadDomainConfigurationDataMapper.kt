package org.rhasspy.mobile.viewmodel.configuration.vad

import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainViewState.VadDomainConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.vad.VadDomainViewState.VadDomainConfigurationData.LocalSilenceDetectionConfigurationData

class VadDomainConfigurationDataMapper {

    operator fun invoke(data: VadDomainData): VadDomainConfigurationData {
        return VadDomainConfigurationData(
            vadDomainOption = data.option,
            localSilenceDetectionSetting = LocalSilenceDetectionConfigurationData(
                silenceDetectionTime = data.automaticSilenceDetectionTime,
                silenceDetectionMinimumTime = data.automaticSilenceDetectionMinimumTime,
                silenceDetectionAudioLevel = data.automaticSilenceDetectionAudioLevel,
            ),
        )
    }

    operator fun invoke(data: VadDomainConfigurationData): VadDomainData {
        return VadDomainData(
            option = data.vadDomainOption,
            automaticSilenceDetectionTime = data.localSilenceDetectionSetting.silenceDetectionTime,
            automaticSilenceDetectionMinimumTime = data.localSilenceDetectionSetting.silenceDetectionMinimumTime,
            automaticSilenceDetectionAudioLevel = data.localSilenceDetectionSetting.silenceDetectionAudioLevel,
        )
    }

}
