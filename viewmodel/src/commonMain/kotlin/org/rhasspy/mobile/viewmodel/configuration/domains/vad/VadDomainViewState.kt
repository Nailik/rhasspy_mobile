package org.rhasspy.mobile.viewmodel.configuration.domains.vad

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.VadDomainOption
import org.rhasspy.mobile.platformspecific.toImmutableList

@Stable
data class VadDomainViewState internal constructor(
    val editData: VadDomainConfigurationData,
) {

    @Stable
    data class VadDomainConfigurationData internal constructor(
        val vadDomainOption: VadDomainOption,
        val localSilenceDetectionSetting: LocalSilenceDetectionConfigurationData,
    ) {

        val vadDomainOptions: ImmutableList<VadDomainOption> = VadDomainOption.entries.toTypedArray().toImmutableList()

        @Stable
        data class LocalSilenceDetectionConfigurationData internal constructor(
            val silenceDetectionTime: String,
            val silenceDetectionMinimumTime: String,
            val silenceDetectionAudioLevel: Float,
        )

    }

}