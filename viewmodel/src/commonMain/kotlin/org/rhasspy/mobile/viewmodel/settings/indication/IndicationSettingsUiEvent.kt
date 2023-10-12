package org.rhasspy.mobile.viewmodel.settings.indication

sealed interface IndicationSettingsUiEvent {

    sealed interface Change : IndicationSettingsUiEvent {

        data class SetWakeWordLightIndicationEnabled(val enabled: Boolean) : Change
        data class SetWakeWordDetectionTurnOnDisplay(val enabled: Boolean) : Change

    }

}