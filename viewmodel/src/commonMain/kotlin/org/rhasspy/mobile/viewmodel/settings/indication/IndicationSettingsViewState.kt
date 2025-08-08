package org.rhasspy.mobile.viewmodel.settings.indication

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.AudioOutputOption

@Stable
data class IndicationSettingsViewState internal constructor(
    val isSoundIndicationEnabled: Boolean,
    val isWakeWordLightIndicationEnabled: Boolean,
    val isWakeWordDetectionTurnOnDisplayEnabled: Boolean,
    val soundIndicationOutputOption: AudioOutputOption,
    val wakeSound: String,
    val recordedSound: String,
    val errorSound: String
) {

    val audioOutputOptionList: ImmutableList<AudioOutputOption> =
        AudioOutputOption.values().toImmutableList()

}