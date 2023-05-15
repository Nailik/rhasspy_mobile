package org.rhasspy.mobile.viewmodel.screens.configuration

sealed interface ConfigurationScreenUiEvent {

    sealed interface Change : ConfigurationScreenUiEvent {

        class SiteIdChange(val text: String) : Change

    }

    sealed interface Action : ConfigurationScreenUiEvent {

        object ScrollToError : Action



    }

    sealed interface Navigate : ConfigurationScreenUiEvent {

        object RemoteHermesHttpClick : Navigate
        object WebserverClick : Navigate
        object MqttClick : Navigate
        object WakeWordClick : Navigate
        object SpeechToTextClick : Navigate
        object IntentRecognitionClick : Navigate
        object TextToSpeechClick : Navigate
        object AudioPlayingClick : Navigate
        object DialogManagementClick : Navigate
        object IntentHandlingClick : Navigate

    }

}