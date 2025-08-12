package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.AudioPlayingOption
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.data.service.option.WakeWordOption

@Stable
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
    val firstErrorIndex: StateFlow<Int?>,
    val scrollToError: Int?,
) {

    @Stable
    data class SiteIdViewState(
        val text: StateFlow<String>,
    )

    @Stable
    data class RemoteHermesHttpViewState(
        val isHttpSSLVerificationEnabled: Boolean,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class WebServerViewState(
        val isHttpServerEnabled: Boolean,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class MqttViewState(
        val isMQTTConnected: Boolean,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class WakeWordViewState(
        val wakeWordValueOption: WakeWordOption,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class SpeechToTextViewState(
        val speechToTextOption: SpeechToTextOption,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class IntentRecognitionViewState(
        val intentRecognitionOption: IntentRecognitionOption,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class TextToSpeechViewState(
        val textToSpeechOption: TextToSpeechOption,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class AudioPlayingViewState(
        val audioPlayingOption: AudioPlayingOption,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class DialogManagementViewState(
        val dialogManagementOption: DialogManagementOption,
        val serviceState: ServiceViewState,
    )

    @Stable
    data class IntentHandlingViewState(
        val intentHandlingOption: IntentHandlingOption,
        val serviceState: ServiceViewState,
    )

}