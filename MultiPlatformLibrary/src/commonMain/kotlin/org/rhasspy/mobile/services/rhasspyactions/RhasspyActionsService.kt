package org.rhasspy.mobile.services.rhasspyactions

import org.koin.core.component.inject
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.interfaces.HomeAssistantInterface
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.LocalAudioService
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.mqtt.MqttService

/**
 * actions are fired and state machine has to react for results in other services
 */
open class RhasspyActionsService : IService() {

    private val params by inject<RhasspyActionsServiceParams>()
    private val localAudioService by inject<LocalAudioService>()
    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()
    private val homeAssistantService by inject<HomeAssistantInterface>()

    /**
     * hermes/nlu/query
     * Request an intent to be recognized from text
     *
     * Response(s)
     * hermes/intent/<intentName>
     * hermes/nlu/intentNotRecognized
     *
     * HTTP:
     * - calls service to recognize intent from text
     * - if IntentHandlingOptions.WithRecognition is set the remote site will also automatically handle the intent
     * - later intentRecognized or intentNotRecognized will be called with received data
     *
     * MQTT:
     * - calls default site to recognize intent
     * - later eventually intentRecognized or intentNotRecognized will be called with received data
     */
    suspend fun recognizeIntent(text: String): Boolean {
        return when (params.intentRecognitionOption) {
            IntentRecognitionOptions.RemoteHTTP -> httpClientService.recognizeIntent(text)
            IntentRecognitionOptions.RemoteMQTT -> mqttClientService.recognizeIntent(text)
            IntentRecognitionOptions.Disabled -> false
        }
    }

    /**
     * hermes/tts/say
     * Does NOT Generate spoken audio for a sentence using the configured text to speech system
     * uses configured Text to speed system to generate audio and then plays it
     *
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     * is called when playing audio is finished
     */
    suspend fun say(text: String): Boolean {
        return when (params.textToSpeechOption) {
            TextToSpeechOptions.RemoteHTTP -> httpClientService.textToSpeech(text)
            TextToSpeechOptions.RemoteMQTT -> mqttClientService.say(text)
            TextToSpeechOptions.Disabled -> false
        }
    }

    /**
     * hermes/audioServer/<siteId>/playBytes/<requestId>
     * Play WAV data
     *
     * Response(s)
     * hermes/audioServer/<siteId>/playFinished (JSON)
     *
     * - if audio output is enabled
     *
     * Local:
     * - play audio with volume set
     *
     * HTTP:
     * - calls service to play audio with wav data
     *
     * MQTT:
     * - calls default site to play audio
     */
    suspend fun playAudio(data: List<Byte>): Boolean {
        return when (params.audioPlayingOption) {
            AudioPlayingOptions.Local -> localAudioService.playAudio(data)
            AudioPlayingOptions.RemoteHTTP -> httpClientService.playWav(data)
            AudioPlayingOptions.RemoteMQTT -> mqttClientService.playBytes(data)
            AudioPlayingOptions.Disabled -> false
        }
    }

    /**
     * Speech to Text (Wav Data)
     * used to translate last spoken
     *
     * HTTP:
     * - calls service to translate speech to text, then handles the intent if dialogue manager is set to local
     *
     * RemoteMQTT
     * - audio was already send to mqtt while recording in audioFrame
     */
    suspend fun speechToText(data: List<Byte>): Boolean {
        return when (params.speechToTextOption) {
            SpeechToTextOptions.RemoteHTTP -> httpClientService.speechToText(data)
            SpeechToTextOptions.RemoteMQTT -> false //nothing to do
            SpeechToTextOptions.Disabled -> false
        }
    }

    /**
     * Only does something if intent handling is enabled
     *
     * HomeAssistant:
     * - calls Home Assistant Service
     *
     * HTTP:
     * - calls service to handle intent
     *
     * WithRecognition
     * - should only be used with HTTP text to intent
     * - remote text to intent will also handle it
     *
     * if local dialogue management it will end the session
     */
    suspend fun intentHandling(intentName: String, intent: String): Boolean {
        return when (params.intentHandlingOption) {
            IntentHandlingOptions.HomeAssistant -> homeAssistantService.sendIntent(intentName, intent)
            IntentHandlingOptions.RemoteHTTP -> httpClientService.intentHandling(intent)
            IntentHandlingOptions.WithRecognition -> false //nothing to do
            IntentHandlingOptions.Disabled -> false
        }
    }
}