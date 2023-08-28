package org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection

sealed interface VoiceActivityDetectionUiEvent {

    sealed interface Change : VoiceActivityDetectionUiEvent

    sealed interface Action : VoiceActivityDetectionUiEvent {

        data object BackClick : Action

    }

}