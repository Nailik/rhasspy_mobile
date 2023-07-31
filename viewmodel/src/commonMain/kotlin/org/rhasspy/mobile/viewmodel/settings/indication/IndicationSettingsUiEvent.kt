package org.rhasspy.mobile.viewmodel.settings.indication

import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.settings.IndicationSettingsScreenDestination

sealed interface IndicationSettingsUiEvent {

    sealed interface Action : IndicationSettingsUiEvent {

        object BackClick : Action
        data class Navigate(val destination: IndicationSettingsScreenDestination) : Action

    }

    sealed interface Change : IndicationSettingsUiEvent {

        data class SetSoundIndicationEnabled(val enabled: Boolean) : Change
        data class SetWakeWordLightIndicationEnabled(val enabled: Boolean) : Change
        data class SetWakeWordDetectionTurnOnDisplay(val enabled: Boolean) : Change
        data class SelectSoundIndicationOutputOption(val option: AudioOutputOption) : Change

    }

}