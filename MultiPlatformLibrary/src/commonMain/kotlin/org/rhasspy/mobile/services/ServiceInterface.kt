package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

object ServiceInterface {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //toggle on off from mqtt or http service
    private val listenForWakeEnabled = MutableLiveData(true)
    val isListenForWakeEnabled = listenForWakeEnabled.readOnly()

    fun textToSpeak(text: String) {

        logger.d { "textToSpeak $text" }

        coroutineScope.launch {

            when (ConfigurationSettings.textToSpeechOption.data) {
                TextToSpeechOptions.RemoteHTTP -> HttpService.textToSpeech(text)
                TextToSpeechOptions.RemoteMQTT -> MqttService.textToSpeak(text)
                TextToSpeechOptions.Disabled -> logger.d { "textToSpeak disabled" }
            }

        }
    }

    fun intentRecognition(text: String) {

        logger.d { "intentRecognition $text" }

        coroutineScope.launch {

            when (ConfigurationSettings.intentRecognitionOption.data) {
                IntentRecognitionOptions.RemoteHTTP -> HttpService.intentRecognition(text)
                IntentRecognitionOptions.RemoteMQTT -> MqttService.intentRecognition(text)
                IntentRecognitionOptions.Disabled -> logger.d { "intentRecognition disabled" }
            }

        }
    }

    fun playAudio(data: ByteArray) {
        logger.d { "playAudio ${data.size}" }

        coroutineScope.launch {

            when (ConfigurationSettings.audioPlayingOption.data) {
                AudioPlayingOptions.Local -> AudioPlayer.playData(data)
                AudioPlayingOptions.RemoteHTTP -> HttpService.playWav(data)
                AudioPlayingOptions.RemoteMQTT -> MqttService.playWav(data)
                AudioPlayingOptions.Disabled -> logger.d { "audioPlaying disabled" }
            }

        }
    }

    fun intentHandling(intent: String) {
        logger.d { "intentRecognized $intent" }

        coroutineScope.launch {

            when (ConfigurationSettings.intentHandlingOption.data) {
                IntentHandlingOptions.HomeAssistant -> TODO()
                IntentHandlingOptions.RemoteHTTP -> HttpService.intentHandling(intent)
                IntentHandlingOptions.Disabled -> logger.d { "intentHandling disabled" }
                IntentHandlingOptions.WithRecognition -> logger.e { "intentHandling with recognition was not used" }
            }

        }
    }

    fun speechToText(data: ByteArray) {
        logger.d { "speechToText ${data.size}" }

        coroutineScope.launch {

            when (ConfigurationSettings.speechToTextOption.data) {
                SpeechToTextOptions.RemoteHTTP -> HttpService.speechToText(data)
                SpeechToTextOptions.RemoteMQTT -> MqttService.speechToText(data)
                SpeechToTextOptions.Disabled -> logger.d { "speechToText disabled" }
            }

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

    fun setVolume(volume: Float) {
        AppSettings.volume.data = volume
    }

    fun setListenForWake(action: Boolean) {
        logger.d { "setListenForWake $action" }

        listenForWakeEnabled.value = action
    }

}