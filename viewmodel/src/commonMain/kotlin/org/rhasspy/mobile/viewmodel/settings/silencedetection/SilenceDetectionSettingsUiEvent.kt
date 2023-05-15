package org.rhasspy.mobile.viewmodel.settings.silencedetection

sealed interface SilenceDetectionSettingsUiEvent {

    sealed interface Change : SilenceDetectionSettingsUiEvent {
        data class SetSilenceDetectionEnabled(val enabled: Boolean) : Change
        data class UpdateSilenceDetectionMinimumTime(val time: String) : Change
        data class UpdateSilenceDetectionTime(val time: String) : Change
        data class UpdateSilenceDetectionAudioLevelPercentage(val percentage: Float) : Change
    }

    sealed interface Navigate : SilenceDetectionSettingsUiEvent {
        object BackClick: Navigate
    }

    sealed interface Action : SilenceDetectionSettingsUiEvent {
        object ToggleAudioLevelTest : Action
    }

}