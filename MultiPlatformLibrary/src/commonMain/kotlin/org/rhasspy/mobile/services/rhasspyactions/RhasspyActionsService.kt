package org.rhasspy.mobile.services.rhasspyactions

import kotlinx.coroutines.*
import org.koin.core.component.inject
import org.rhasspy.mobile.addWavHeader
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService
import org.rhasspy.mobile.settings.option.*

//TODO logging
/**
 * calls actions and returns result
 *
 * when data is null the service was most probably mqtt and will return result in a call function
 */
open class RhasspyActionsService : IService() {

    private val params by inject<RhasspyActionsServiceParams>()

    private val localAudioService by inject<LocalAudioService>()
    private val httpClientService by inject<HttpClientService>()
    private val mqttClientService by inject<MqttService>()
    private val homeAssistantService by inject<HomeAssistantService>()
    private val recordingService by inject<RecordingService>()

    private val serviceMiddleware by inject<IServiceMiddleware>()

    private val _speechToTextAudioData = mutableListOf<Byte>()
    val speechToTextAudioData: List<Byte> get() = _speechToTextAudioData

    private val scope = CoroutineScope(Dispatchers.Default)
    private var collector: Job? = null

    override fun onClose() {
        //nothing to do
        scope.cancel()
        collector?.cancel()
    }

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
    suspend fun recognizeIntent(sessionId: String, text: String) {
        when (params.intentRecognitionOption) {
            IntentRecognitionOption.RemoteHTTP -> {
                val action = httpClientService.recognizeIntent(text)?.let { intentJson ->
                    DialogAction.IntentRecognitionResult(Source.HttpApi, "", intentJson)
                } ?: run {
                    DialogAction.IntentRecognitionError(Source.HttpApi)
                }
                serviceMiddleware.action(action)
            }
            IntentRecognitionOption.RemoteMQTT -> mqttClientService.recognizeIntent(sessionId, text)
            IntentRecognitionOption.Disabled -> {}
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
    suspend fun textToSpeech(sessionId: String, text: String) {
        when (params.textToSpeechOption) {
            TextToSpeechOption.RemoteHTTP -> {
                httpClientService.textToSpeech(text)?.also {
                    serviceMiddleware.action(DialogAction.PlayAudio(Source.HttpApi, it))
                }
            }
            TextToSpeechOption.RemoteMQTT -> mqttClientService.say(sessionId, text)
            TextToSpeechOption.Disabled -> {}
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
    suspend fun playAudio(data: List<Byte>) {
        when (params.audioPlayingOption) {
            AudioPlayingOption.Local -> {
                localAudioService.playAudio(data)
                serviceMiddleware.action(DialogAction.PlayFinished(Source.Local))
            }
            AudioPlayingOption.RemoteHTTP -> {
                httpClientService.playWav(data)
                serviceMiddleware.action(DialogAction.PlayFinished(Source.HttpApi))
            }
            AudioPlayingOption.RemoteMQTT -> mqttClientService.playBytes(data)
            AudioPlayingOption.Disabled -> {}
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
     *
     * fromMqtt is used to check if silence was detected by remote mqtt device
     */
    suspend fun endSpeechToText(sessionId: String, fromMqtt: Boolean) {
        //stop collection
        collector?.cancel()

        //evaluate result
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {
                val action = httpClientService.speechToText(_speechToTextAudioData.addWavHeader())?.let { text ->
                    DialogAction.AsrTextCaptured(Source.HttpApi, text)
                } ?: run {
                    DialogAction.AsrError(Source.HttpApi)
                }
                serviceMiddleware.action(action)
            }
            SpeechToTextOption.RemoteMQTT -> if (!fromMqtt) mqttClientService.stopListening(sessionId)
            SpeechToTextOption.Disabled -> {}
        }

        //clear data
        _speechToTextAudioData.clear()
    }

    suspend fun startSpeechToText(sessionId: String) {
        //clear data and start recording
        collector?.cancel()
        _speechToTextAudioData.clear()

        if (params.speechToTextOption != SpeechToTextOption.Disabled) {
            //start collection
            collector = scope.launch {
                recordingService.output.collect(::audioFrame)
            }
        }

        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> {}
            SpeechToTextOption.RemoteMQTT -> mqttClientService.startListening(sessionId)
            SpeechToTextOption.Disabled -> {}
        }
    }

    private suspend fun audioFrame(data: List<Byte>) {
        when (params.speechToTextOption) {
            SpeechToTextOption.RemoteHTTP -> _speechToTextAudioData.addAll(data)
            SpeechToTextOption.RemoteMQTT -> mqttClientService.audioFrame(
                data.toMutableList().addWavHeader()
            )
            SpeechToTextOption.Disabled -> {}//nothing to do
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
    suspend fun intentHandling(intentName: String, intent: String) {
        when (params.intentHandlingOption) {
            IntentHandlingOption.HomeAssistant -> homeAssistantService.sendIntent(intentName, intent)
            IntentHandlingOption.RemoteHTTP -> httpClientService.intentHandling(intent)
            IntentHandlingOption.WithRecognition -> {}
            IntentHandlingOption.Disabled -> {}
        }
    }

}