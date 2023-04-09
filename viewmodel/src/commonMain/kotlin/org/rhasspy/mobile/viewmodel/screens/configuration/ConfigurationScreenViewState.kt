package org.rhasspy.mobile.viewmodel.screens.configuration

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.ScrollToErrorEvent

data class ConfigurationScreenViewState(
    val siteId: SiteIdViewState,
    val remoteHermesHttp: RemoteHermesHttpViewState,
    val webserver: WebServerViewState,
    val mqtt: MqttViewState,
    val wakeWord: WakeWordViewState,
    val speechToText: SpeechToTextViewState,
    val intentRecognition: IntentRecognitionViewState,
    val textToSpeech: TextToSpeechViewState,
    val audioPlaying: AudioPlayingViewState,
    val dialogManagement: DialogManagementViewState,
    val intentHandling: IntentHandlingViewState,
    val hasError: StateFlow<Boolean>,
    val scrollToErrorEvent: ScrollToErrorEvent
) {

    data class SiteIdViewState(
        val text: StateFlow<String>
    )

    data class RemoteHermesHttpViewState(
        val isHttpSSLVerificationEnabled: Boolean,
        val serviceState: StateFlow<ServiceState>
    )

    data class WebServerViewState(
        val isHttpServerEnabled: Boolean,
        val serviceState: StateFlow<ServiceState>
    )

    data class MqttViewState(
        val isMQTTConnected: StateFlow<Boolean>,
        val serviceState: StateFlow<ServiceState>
    )

    data class WakeWordViewState(
        val wakeWordValueOption: WakeWordOption,
        val serviceState: StateFlow<ServiceState>
    )

    data class SpeechToTextViewState(
        val speechToTextOption: SpeechToTextOption,
        val serviceState: StateFlow<ServiceState>
    )

    data class IntentRecognitionViewState(
        val intentRecognitionOption: IntentRecognitionOption,
        val serviceState: StateFlow<ServiceState>
    )

    data class TextToSpeechViewState(
        val textToSpeechOption: TextToSpeechOption,
        val serviceState: StateFlow<ServiceState>
    )

    data class AudioPlayingViewState(
        val audioPlayingOption: AudioPlayingOption,
        val serviceState: StateFlow<ServiceState>
    )

    data class DialogManagementViewState(
        val dialogManagementOption: DialogManagementOption,
        val serviceState: StateFlow<ServiceState>
    )

    data class IntentHandlingViewState(
        val intentHandlingOption: IntentHandlingOption,
        val serviceState: StateFlow<ServiceState>
    )

}