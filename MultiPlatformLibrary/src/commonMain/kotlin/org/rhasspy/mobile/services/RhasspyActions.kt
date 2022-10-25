package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.logic.StateMachine
import org.rhasspy.mobile.serviceInterfaces.HomeAssistantInterface
import org.rhasspy.mobile.serviceInterfaces.HttpClientInterface
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

/**
 * handles different actions that are done for rhasspy
 *
 * recognizeIntent
 * Speak
 * playAudio
 * handleIntent
 * speechtotext
 */
@ThreadLocal
object RhasspyActions {

    private val logger = Logger.withTag("RhasspyActions")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

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
    fun recognizeIntent(text: String) {

        logger.d { "recognizeIntent $text" }

        coroutineScope.launch {
            when (ConfigurationSettings.intentRecognitionOption.value) {
                IntentRecognitionOptions.RemoteHTTP -> {
                    //get intent from http endpoint
                    val handleDirectly = ConfigurationSettings.intentHandlingOption.value == IntentHandlingOptions.WithRecognition
                    val intent = HttpClientInterface.intentRecognition(text, handleDirectly)

                    if (!handleDirectly && ConfigurationSettings.dialogManagementOption.value == DialogManagementOptions.Local) {
                        //if intent wasn't already handled and local dialogue management, handle it
                        val json = intent?.let { Json.decodeFromString<JsonObject>(intent) }
                        val intentName = json?.get("intent")?.jsonObject?.get("name")?.jsonPrimitive?.content ?: ""

                        if (intentName.isEmpty() || intent == null) {
                            StateMachine.intentNotRecognized()
                        } else {
                            StateMachine.intentRecognized(intentName, intent)
                        }
                    }

                    if (handleDirectly && ConfigurationSettings.dialogManagementOption.value == DialogManagementOptions.Local) {
                        //if intent was handled directly and local dialogue management it's time to end dialogue
                        StateMachine.endSession()
                    }
                }
                //send intent to mqtt service
                IntentRecognitionOptions.RemoteMQTT -> MqttService.intentQuery(StateMachine.currentSession.sessionId, text)
                IntentRecognitionOptions.Disabled -> {
                    logger.d { "intentRecognition disabled" }
                    if (ConfigurationSettings.dialogManagementOption.value == DialogManagementOptions.Local) {
                        StateMachine.intentNotRecognized()
                    }

                }
            }
        }
    }

    /**
     * hermes/tts/say
     * Does NOT Generate spoken audio for a sentence using the configured text to speech system
     * uses configured Text to speed system to generate audio and then plays it
     *
     * Response(s)
     * hermes/tts/sayFinished (JSON)
     * is called when generating audio is finished
     */
    fun say(text: String) {
        logger.d { "say $text" }

        coroutineScope.launch {
            when (ConfigurationSettings.textToSpeechOption.value) {
                TextToSpeechOptions.RemoteHTTP -> {
                    //use remote text to speech to get audio data and then play it
                    HttpClientInterface.textToSpeech(text)?.also {
                        playAudio(it)
                    }
                }
                //when mqtt is used, say will published and automatically playBytes will be invoked on this siteId
                TextToSpeechOptions.RemoteMQTT -> MqttService.say(StateMachine.currentSession.sessionId, text)
                TextToSpeechOptions.Disabled -> logger.d { "textToSpeech disabled" }
            }
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
    fun playAudio(data: List<Byte>) {
        logger.d { "playAudio ${data.size}" }

        if (AppSettings.isAudioOutputEnabled.value) {
            coroutineScope.launch {
                when (ConfigurationSettings.audioPlayingOption.value) {
                    AudioPlayingOptions.Local -> StateMachine.playAudio(data, true)
                    AudioPlayingOptions.RemoteHTTP -> HttpClientInterface.playWav(data)
                    AudioPlayingOptions.RemoteMQTT -> MqttService.playBytes(data)
                    AudioPlayingOptions.Disabled -> logger.d { "audioPlaying disabled" }
                }
            }
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
    fun speechToText() {
        logger.d { "speechToText" }

        coroutineScope.launch {

            when (ConfigurationSettings.speechToTextOption.value) {
                //send the recording to the http endpoint
                SpeechToTextOptions.RemoteHTTP -> HttpClientInterface.speechToText(StateMachine.getPreviousRecording())?.also {
                    StateMachine.intentTranscribed(intent = it)
                } ?: run {
                    StateMachine.intentTranscriptionError()
                }
                //when speech to text mqtt is used, then speech is send in chunks while recording
                SpeechToTextOptions.RemoteMQTT -> logger.v { "speechToText already send to mqtt" }
                SpeechToTextOptions.Disabled -> StateMachine.intentTranscriptionError()
            }
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
    fun intentHandling(intentName: String, intent: String) {

        if (AppSettings.isIntentHandlingEnabled.value) {
            coroutineScope.launch {
                when (ConfigurationSettings.intentHandlingOption.value) {
                    IntentHandlingOptions.HomeAssistant -> HomeAssistantInterface.sendIntent(intentName, intent)
                    IntentHandlingOptions.RemoteHTTP -> HttpClientInterface.intentHandling(intent)
                    IntentHandlingOptions.WithRecognition -> logger.v { "intentHandling with recognition was used" }
                    IntentHandlingOptions.Disabled -> logger.d { "intentHandling disabled" }
                }
            }
        }
    }

}