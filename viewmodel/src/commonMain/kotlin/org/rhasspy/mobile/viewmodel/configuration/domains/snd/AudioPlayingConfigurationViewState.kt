package org.rhasspy.mobile.viewmodel.configuration.domains.snd

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.service.option.SndDomainOption

@Stable
data class AudioPlayingConfigurationViewState internal constructor(
    val editData: SndDomainConfigurationData
) {

    @Stable
    data class SndDomainConfigurationData internal constructor(
        val sndDomainOption: SndDomainOption,
        val audioOutputOption: AudioOutputOption,
        val audioPlayingMqttSiteId: String,
        val audioTimeout: String,
        val rhasspy2HermesMqttTimeout: String,
    ) {

        val sndDomainOptionLists: ImmutableList<SndDomainOption> = SndDomainOption.entries.toImmutableList()
        val audioOutputOptionList: ImmutableList<AudioOutputOption> = AudioOutputOption.entries.toImmutableList()

    }

}