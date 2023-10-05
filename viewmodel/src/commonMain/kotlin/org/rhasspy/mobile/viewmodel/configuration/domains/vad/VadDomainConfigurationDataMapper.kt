package org.rhasspy.mobile.viewmodel.configuration.domains.vad

import org.rhasspy.mobile.data.data.toLongOrZero
import org.rhasspy.mobile.data.domain.VadDomainData
import org.rhasspy.mobile.viewmodel.configuration.domains.vad.VadDomainViewState.VadDomainConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.domains.vad.VadDomainViewState.VadDomainConfigurationData.LocalSilenceDetectionConfigurationData
import kotlin.time.Duration.Companion.milliseconds

class VadDomainConfigurationDataMapper {

    operator fun invoke(data: VadDomainData): VadDomainConfigurationData {
        return VadDomainConfigurationData(
            vadDomainOption = data.option,
            localSilenceDetectionSetting = LocalSilenceDetectionConfigurationData(
                silenceDetectionTime = data.automaticSilenceDetectionTime.inWholeMilliseconds.toString(),
                silenceDetectionMinimumTime = data.automaticSilenceDetectionMinimumTime.inWholeMilliseconds.toString(),
                silenceDetectionAudioLevel = data.automaticSilenceDetectionAudioLevel,
            ),
        )
    }

    operator fun invoke(data: VadDomainConfigurationData): VadDomainData {
        return VadDomainData(
            option = data.vadDomainOption,
            automaticSilenceDetectionTime = data.localSilenceDetectionSetting.silenceDetectionTime.toLongOrZero().milliseconds,
            automaticSilenceDetectionMinimumTime = data.localSilenceDetectionSetting.silenceDetectionMinimumTime.toLongOrZero().milliseconds,
            automaticSilenceDetectionAudioLevel = data.localSilenceDetectionSetting.silenceDetectionAudioLevel,
        )
    }

}