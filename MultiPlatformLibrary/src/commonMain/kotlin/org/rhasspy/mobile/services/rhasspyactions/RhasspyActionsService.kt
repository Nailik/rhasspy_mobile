package org.rhasspy.mobile.services.rhasspyactions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.addWavHeader
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.middleware.ErrorType.RhasspyActionsServiceErrorType.Disabled
import org.rhasspy.mobile.middleware.ErrorType.RhasspyActionsServiceErrorType.NotInitialized
import org.rhasspy.mobile.middleware.Event
import org.rhasspy.mobile.middleware.EventType.RhasspyActionServiceEventType.*
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.nativeutils.AudioRecorder
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.ServiceResponse
import org.rhasspy.mobile.services.homeassistant.HomeAssistantService
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.localaudio.LocalAudioService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.recording.RecordingService

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

    private val speechToTextAudioData = mutableListOf<Byte>()
    private var saveAudioForSpeechToText = false

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            AudioRecorder.output.collect {
                audioFrame(it)
            }
        }
    }

    override fun onClose() {
        //nothing to do
        scope.cancel()
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
    suspend fun recognizeIntent(sessionId: String, text: String): ServiceResponse<*> {
        val event = serviceMiddleware.createEvent(RecognizeIntent)

        val result = when (params.intentRecognitionOption) {
            IntentRecognitionOptions.RemoteHTTP -> httpClientService.recognizeIntent(text)
            IntentRecognitionOptions.RemoteMQTT -> mqttClientService.recognizeIntent(sessionId, text)
            IntentRecognitionOptions.Disabled -> ServiceResponse.Disabled
        }
        event.evaluateResult(result)
        return result
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
    suspend fun textToSpeech(sessionId: String, text: String): ServiceResponse<*> {
        val event = serviceMiddleware.createEvent(Say)

        val result = when (params.textToSpeechOption) {
            TextToSpeechOptions.RemoteHTTP -> httpClientService.textToSpeech(text)
            TextToSpeechOptions.RemoteMQTT -> mqttClientService.say(sessionId, text)
            TextToSpeechOptions.Disabled -> ServiceResponse.Disabled
        }
        event.evaluateResult(result)
        return result
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
    suspend fun playAudio(data: List<Byte>): ServiceResponse<*> {
        val event = serviceMiddleware.createEvent(PlayAudio)

        val result = when (params.audioPlayingOption) {
            AudioPlayingOptions.Local -> localAudioService.playAudio(data)
            AudioPlayingOptions.RemoteHTTP -> httpClientService.playWav(data)
            AudioPlayingOptions.RemoteMQTT -> mqttClientService.playBytes(data)
            AudioPlayingOptions.Disabled -> ServiceResponse.Disabled
        }
        event.evaluateResult(result)
        return result
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
    suspend fun endSpeechToText(sessionId: String): ServiceResponse<*> {
        saveAudioForSpeechToText = false
        recordingService.stopRecording()
        val event = serviceMiddleware.createEvent(SpeechToText)

        val result = when (params.speechToTextOption) {
            SpeechToTextOptions.RemoteHTTP -> httpClientService.speechToText(speechToTextAudioData.addWavHeader())
            SpeechToTextOptions.RemoteMQTT -> mqttClientService.stopListening(sessionId) //TODO eventually send silent data
            SpeechToTextOptions.Disabled -> ServiceResponse.Disabled
        }

        speechToTextAudioData.clear()
        event.evaluateResult(result)
        return result
    }

    suspend fun startSpeechToText(sessionId: String): ServiceResponse<*> {
        saveAudioForSpeechToText = true
        recordingService.startRecording()
        speechToTextAudioData.clear()

        val event = serviceMiddleware.createEvent(SpeechToText)
        val result = when (params.speechToTextOption) {
            SpeechToTextOptions.RemoteHTTP -> ServiceResponse.Nothing
            SpeechToTextOptions.RemoteMQTT -> mqttClientService.startListening(sessionId) //TODO eventually hot word
            SpeechToTextOptions.Disabled -> ServiceResponse.Nothing
        }
        event.evaluateResult(result)
        return result
    }

    suspend fun audioFrame(data: List<Byte>) {
        if (saveAudioForSpeechToText) {
            when (params.speechToTextOption) {
                SpeechToTextOptions.RemoteHTTP -> speechToTextAudioData.addAll(data)
                SpeechToTextOptions.RemoteMQTT -> mqttClientService.audioFrame(data.toMutableList().addWavHeader())
                SpeechToTextOptions.Disabled -> {}//nothing to do
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
    suspend fun intentHandling(intentName: String, intent: String): ServiceResponse<*> {
        val event = serviceMiddleware.createEvent(IntentHandling)

        val result = when (params.intentHandlingOption) {
            IntentHandlingOptions.HomeAssistant -> homeAssistantService.sendIntent(intentName, intent)
            IntentHandlingOptions.RemoteHTTP -> httpClientService.intentHandling(intent)
            IntentHandlingOptions.WithRecognition -> ServiceResponse.Nothing
            IntentHandlingOptions.Disabled -> ServiceResponse.Disabled
        }
        event.evaluateResult(result)
        return result
    }

    /**
     * update event depending on result of action
     */
    private fun Event.evaluateResult(result: ServiceResponse<*>) {
        when (result) {
            is ServiceResponse.Success -> this.success(result.data.toString())
            is ServiceResponse.Nothing -> this.success()
            is ServiceResponse.Disabled -> this.warning(Disabled)
            is ServiceResponse.Error -> this.error(result.error)
            is ServiceResponse.NotInitialized -> this.error(NotInitialized)
        }
    }
}