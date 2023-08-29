package org.rhasspy.mobile.viewmodel.configuration.audioinput

sealed interface AudioInputConfigurationUiEvent {

    sealed interface Action : AudioInputConfigurationUiEvent {

        data object BackClick : Action
        data object OpenWakeWordRecorderFormatScreen : Action
        data object OpenWakeWordOutputFormatScreen : Action
        data object OpenTextToSpeechRecorderFormatScreen : Action
        data object OpenTextToSpeechOutputFormatScreen : Action

    }

}