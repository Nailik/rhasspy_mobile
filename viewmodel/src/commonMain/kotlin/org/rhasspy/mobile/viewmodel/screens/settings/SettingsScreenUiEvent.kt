package org.rhasspy.mobile.viewmodel.screens.settings

sealed interface SettingsScreenUiEvent {

    sealed interface Navigate : SettingsScreenUiEvent {

        object LanguageClick : Navigate
        object BackgroundServiceClick : Navigate
        object MicrophoneOverlayClick : Navigate
        object IndicationClick : Navigate
        object DeviceClick : Navigate
        object AudioFocusClick : Navigate
        object AudioRecorderSettingsClick : Navigate
        object AutomaticSilenceDetectionClick : Navigate
        object LogClick : Navigate
        object SaveAndRestoreClick : Navigate
        object AboutClick : Navigate

    }

}