package org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.sounds.IndicationSoundOption

@Stable
data class IndicationSoundConfigurationViewState internal constructor(
    val editData: IndicationSoundOptionConfigurationData,
    val audioPlayerViewState: StateFlow<AudioPlayerViewState>,
    val snackBarText: StableStringResource?,
) {

    @Stable
    data class IndicationSoundOptionConfigurationData(
        val volume: Float,
        val option: IndicationSoundOption,
    )

}