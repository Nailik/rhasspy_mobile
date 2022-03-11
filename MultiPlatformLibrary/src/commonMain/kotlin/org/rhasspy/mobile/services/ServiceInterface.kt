package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.settings.ConfigurationSettings

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

    fun speechToText(data: ByteArray) {
        logger.d { "speechToText ${data.size}" }

        coroutineScope.launch {

            when (ConfigurationSettings.speechToTextOption.data) {
                SpeechToTextOptions.RemoteHTTP -> ExternalHttpService.speechToText(data)
                SpeechToTextOptions.RemoteMQTT -> TODO()
                SpeechToTextOptions.Disabled -> logger.d { "speechToText disabled" }
            }

        }
    }

    fun receivedTextFromSpeech(text: String) {
        logger.d { "receivedTextFromSpeech $text" }

        intentRecognition(text)
    }

    fun toggleRecording() {
        logger.d { "toggleRecording" }

        if (RecordingService.status.value) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    fun startRecording() {
        logger.d { "startRecording" }

        RecordingService.startRecording()
    }

    fun stopRecording() {
        logger.d { "stopRecording" }

        RecordingService.stopRecording()
        speechToText(RecordingService.getLatestRecording())
    }

    fun registeredSilence() {
        stopRecording()
    }

}