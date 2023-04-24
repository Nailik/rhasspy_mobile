package org.rhasspy.mobile.viewmodel.settings.indication

import org.rhasspy.mobile.data.service.option.AudioOutputOption

sealed interface IndicationSettingsUiEvent {

    sealed interface Change : IndicationSettingsUiEvent {
        data class SetSoundIndicationEnabled(val enabled: Boolean) : Change
        data class SetWakeWordLightIndicationEnabled(val enabled: Boolean) : Change
        data class SetWakeWordDetectionTurnOnDisplay(val enabled: Boolean) : Change
        data class SelectSoundIndicationOutputOption(val option: AudioOutputOption) : Change
    }

}