package org.rhasspy.mobile.viewmodel.settings.indication

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.toImmutableList

data class IndicationSettingsViewState(
    val isSoundIndicationEnabled: Boolean = AppSetting.isSoundIndicationEnabled.value,
    val isWakeWordLightIndicationEnabled: Boolean = AppSetting.isWakeWordLightIndicationEnabled.value,
    val isWakeWordDetectionTurnOnDisplayEnabled: Boolean = AppSetting.isWakeWordDetectionTurnOnDisplayEnabled.value,
    val soundIndicationOutputOption: AudioOutputOption = AppSetting.soundIndicationOutputOption.value,
    val wakeSound: String = AppSetting.wakeSound.value,
    val recordedSound: String = AppSetting.recordedSound.value,
    val errorSound: String = AppSetting.errorSound.value
) {

    val audioOutputOptionList: ImmutableList<AudioOutputOption> get() = AudioOutputOption.values().toImmutableList()

}