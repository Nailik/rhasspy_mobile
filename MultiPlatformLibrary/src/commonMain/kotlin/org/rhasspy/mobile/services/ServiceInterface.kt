package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.data.TextToSpeechOptions
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.time.Duration.Companion.seconds

object ServiceInterface {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun textToSpeak(text: String) {

        logger.d { "textToSpeak $text" }

        coroutineScope.launch {

            when (ConfigurationSettings.textToSpeechOption.data) {
                TextToSpeechOptions.RemoteHTTP -> ExternalHttpService.textToSpeech(text)
                TextToSpeechOptions.RemoteMQTT -> TODO()
                TextToSpeechOptions.Disabled -> logger.d { "textToSpeak disabled" }
            }

        }
    }

    fun intentRecognition(text: String) {

        logger.d { "intentRecognition $text" }

        coroutineScope.launch {

            when (ConfigurationSettings.intentRecognitionOption.data) {
                IntentRecognitionOptions.RemoteHTTP -> ExternalHttpService.intentRecognition(text)
                IntentRecognitionOptions.RemoteMQTT -> TODO()
                IntentRecognitionOptions.Disabled -> logger.d { "intentRecognition disabled" }
            }

        }
    }

    fun playAudio(data: ByteArray) {
        logger.d { "playAudio ${data.size}" }

        coroutineScope.launch {

            when (ConfigurationSettings.audioPlayingOption.data) {
                AudioPlayingOptions.Local -> AudioPlayer.playData(data)
                AudioPlayingOptions.RemoteHTTP -> ExternalHttpService.playWav(data)
                AudioPlayingOptions.RemoteMQTT -> TODO()
                AudioPlayingOptions.Disabled -> logger.d { "audioPlaying disabled" }
            }

        }
    }

    fun intentHandling(intent: String) {
        logger.d { "intentRecognized $intent" }

        coroutineScope.launch {

            when (ConfigurationSettings.intentHandlingOption.data) {
                IntentHandlingOptions.HomeAssistant -> TODO()
                IntentHandlingOptions.RemoteHTTP -> ExternalHttpService.intentHandling(intent)
                IntentHandlingOptions.Disabled -> logger.d { "intentHandling disabled" }
                IntentHandlingOptions.WithRecognition -> logger.e { "intentHandling with recognition was not used" }
            }

        }
    }

    fun toggleRecording() {
        RecordingService.toggleRecording()
    }

    fun startRecording() {
        //start recording
        RecordingService.startRecording()
    }

}