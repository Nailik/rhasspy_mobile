package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.*

@Stable
data class ConfigurationScreenViewState internal constructor(
    val siteId: SiteIdViewState,
    val dialogPipeline: DialogPipelineViewState,
    val audioInput: AudioInputViewState,
    val wakeWord: WakeWordViewState,
    val speechToText: SpeechToTextViewState,
    val voiceActivityDetection: VoiceActivityDetectionViewState,
    val intentRecognition: IntentRecognitionViewState,
    val intentHandling: IntentHandlingViewState,
    val textToSpeech: TextToSpeechViewState,
    val audioPlaying: AudioPlayingViewState,
    val hasError: StateFlow<Boolean>,
) {

    @Stable
    data class SiteIdViewState internal constructor(
        val text: StateFlow<String>
    )

    @Stable
    data class DialogPipelineViewState internal constructor(
        val dialogManagementOption: DialogManagementOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class AudioInputViewState internal constructor(
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
    data class VoiceActivityDetectionViewState internal constructor(
        val voiceActivityDetectionOption: VoiceActivityDetectionOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class IntentRecognitionViewState internal constructor(
        val intentRecognitionOption: IntentRecognitionOption,
        val serviceState: ServiceViewState
    )

    @Stable
    data class IntentHandlingViewState internal constructor(
        val intentHandlingOption: IntentHandlingOption,
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
}