package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.*

@Stable
data class ConfigurationScreenViewState internal constructor(
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
    val firstErrorIndex: StateFlow<Int?>,
    val scrollToError: Int?
) {

    @Stable
    data class SiteIdViewState internal constructor(
        val text: StateFlow<String>
    )

    @Stable
    data class RemoteHermesHttpViewState internal constructor(
        val isHttpSSLVerificationEnabled: Boolean,
        val serviceState: ServiceViewState
    )

    @Stable
    data class WebServerViewState internal constructor(
        val isHttpServerEnabled: Boolean,
        val serviceState: ServiceViewState
    )

    @Stable
    data class MqttViewState internal constructor(
        val isMQTTConnected: Boolean,
        val serviceState: ServiceViewState
    )

    @Stable
    data class WakeWordViewState internal constructor(
        val wakeWordValueOption: WakeWordOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class SpeechToTextViewState internal constructor(
        val speechToTextOption: SpeechToTextOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class IntentRecognitionViewState internal constructor(
        val intentRecognitionOption: IntentRecognitionOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class TextToSpeechViewState internal constructor(
        val textToSpeechOption: TextToSpeechOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class AudioPlayingViewState internal constructor(
        val audioPlayingOption: AudioPlayingOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class DialogManagementViewState internal constructor(
        val dialogManagementOption: DialogManagementOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class IntentHandlingViewState internal constructor(
        val intentHandlingOption: IntentHandlingOption,
        val serviceState: ServiceViewState
    )

}