package org.rhasspy.mobile.viewmodel.configuration.remotehermeshttp

sealed interface RemoteHermesHttpConfigurationUiEvent {

    sealed interface Change : RemoteHermesHttpConfigurationUiEvent {
        data class UpdateHttpClientServerEndpointHost(val host: String) : Change
        data class UpdateHttpClientTimeout(val text: String) : Change
        data class UpdateHttpClientServerEndpointPort(val port: String) : Change
        data class SetHttpSSLVerificationDisabled(val disabled: Boolean) : Change
        data class UpdateTestRemoteHermesHttpIntentRecognitionText(val text: String) : Change
        data class UpdateTestRemoteHermesHttpTextToSpeechText(val text: String) : Change

    }

    sealed interface Action : RemoteHermesHttpConfigurationUiEvent {

        object TestRemoteHermesHttpToggleRecording : Action
        object TestRemoteHermesHttpIntentRecognitionTest : Action
        object TestRemoteHermesHttpTextToSpeechTest : Action


    }

}