package org.rhasspy.mobile.viewmodel.settings.indication

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.platformspecific.toImmutableList

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

    val audioOutputOptionList: ImmutableList<AudioOutputOption> = AudioOutputOption.values().toImmutableList()

}