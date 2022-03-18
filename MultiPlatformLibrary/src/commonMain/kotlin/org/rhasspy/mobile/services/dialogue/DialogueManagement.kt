package org.rhasspy.mobile.services.dialogue

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.services.*
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object DialogueManagement {
    private val logger = Logger.withTag(this::class.simpleName!!)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //toggle on off from mqtt or http service
    private var isIntentRecognized = false

    private var previousRecording = listOf<Byte>()
    private var currentRecording = mutableListOf<Byte>()

    /**
     * not null if there is currently a session
     */
    private var currentSessionId: MutableLiveData<String?> = MutableLiveData(null)

    var sessionId = currentSessionId.readOnly()

    /**
     * hermes/dialogueManager/startSession
     * Starts a new dialogue session (done automatically on hotword detected)
     * only handeled when DialogueManagement is local
     *
     * Response(s)
     * hermes/dialogueManager/sessionStarted
     */
    fun startSession() {
        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            coroutineScope.launch {
                val sessionUuid = uuid4()
                //send response
                MqttService.sessionStarted(sessionUuid.toString())
                //start the session
                sessionStarted(sessionUuid.toString())
            }
        }
    }

    /**
     * hermes/dialogueManager/endSession
     * Requests that a session be terminated nominally
     */
    fun endSession(sessionId: String?) {
        if (sessionId != this.currentSessionId.value) {
            logger.e { "trying to end session with invalid id" }
            return
        }

        //stop listening just in case it's still running
        stopListening()

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            coroutineScope.launch {
                currentSessionId.value?.also {
                    //send response
                    MqttService.sessionEnded(it)
                    //start the session
                    sessionEnded(it)
                }
            }
        }
    }

    /**
     * hermes/dialogueManager/sessionStarted
     * Indicates a session has started
     *
     * Response to [hermes/dialogueManager/startSession]
     *
     * internal dialogue manager will disable hotWord and start recording now
     */
    fun sessionStarted(sessionId: String) {
        isIntentRecognized = false
        currentSessionId.value = sessionId

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            hotWordToggle(false)
            startListening()
        }
    }

    /**
     * hermes/dialogueManager/sessionEnded
     * Indicates a session has terminated
     *
     * Response to hermes/dialogueManager/endSession or other reasons for a session termination
     *
     * sessionId will be reset
     * and internal dialogue manager will make sure that recording is stopped
     */
    suspend fun sessionEnded(sessionId: String) {
        currentSessionId.value = null

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            stopListening()
        }
        if (!isIntentRecognized) {
            MqttService.sessionIntentNotRecognized()
        }
    }

    /**
     * hermes/audioServer/<siteId>/audioFrame
     * WAV chunk from microphone
     */
    fun audioFrame(byteArray: ByteArray) {
        //send to udp if udp streaming
        if (AppSettings.isAudioOutputEnabled.data) {
            UdpService.streamAudio(byteArray)
        }

        //send to mqtt if mqtt listen for WakeWord or mqtt text to speech
        if (ConfigurationSettings.wakeWordOption.value == WakeWordOption.MQTT ||
            ConfigurationSettings.speechToTextOption.value == SpeechToTextOptions.RemoteMQTT
        ) {
            MqttService.audioFrame(byteArray)
        }

        //if there is a current session record this audio to save if for intent recognition
    }

    /**
     * hermes/hotword/toggleOn
     * Enables wake word detection
     *
     * hermes/hotword/toggleOff
     * Disables wake word detection
     */
    fun hotWordToggle(onOff: Boolean) {
        AppSettings.isHotWordEnabled.data = onOff
    }

    /**
     * hermes/wake/hotword/<wakewordId>/detected
     * Indicates a hotword was successfully detected
     *
     * if local dialogue management it will start a session
     */
    fun hotWordDetected() {
        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            startSession()
        }
        MqttService.hotWordDetected()
    }

    /**
     * hermes/error/hotword
     * Sent when an error occurs in the hotword system
     *
     * used when there is an error in the porcupine system
     */
    fun hotWordError(description: String) {
        MqttService.hotWordError(description)
    }

    /**
     * hermes/asr/startListening
     * Tell ASR system to start recording/transcribing
     *
     * used to start recording
     * resets current recording
     */
    fun startListening() {
        ServiceInterface.indication(true)
        currentRecording.clear()
        RecordingService.startRecording()
    }


    /**
     * hermes/asr/stopListening
     * Tell ASR system to stop recording
     *
     * Emits textCaptured if silence was not detected earlier
     *
     * used to stop recording
     * if local dialogue management it will also enable hotWord again
     * it will also try to convert speech to text
     *
     * saves currentRecording to previous
     */
    fun stopListening() {
        ServiceInterface.indication(false)
        RecordingService.stopRecording()
        //independent copy of current recording
        previousRecording = mutableListOf<Byte>().apply { addAll(currentRecording) }

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            hotWordToggle(true)
            speechToText()
        }
    }

    /**
     * hermes/asr/textCaptured
     * Successful transcription, sent either when silence is detected or on stopListening
     *
     * listening will be stopped
     * if local dialogue management it will try to recognize intent from text
     */
    fun asrTextCaptured(text: String) {
        stopListening()

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            recognizeIntent(text)
        }
    }

    /**
     * hermes/error/asr
     * Sent when an error occurs in the ASR system
     *
     * listening will be stopped
     * if local dialogue management it will end the session
     */
    fun asrError(text: String) {
        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            //also stops listening
            endSession(currentSessionId.value)
        } else {
            stopListening()
        }
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
     * - later [intentRecognized] of [intentNotRecognized] will be called with received data
     *
     * MQTT:
     * - calls default site to recognize intent
     * - later eventually [intentHandling] will be called with received data
     */
    fun recognizeIntent(text: String) {
        coroutineScope.launch {
            when (ConfigurationSettings.intentRecognitionOption.data) {
                IntentRecognitionOptions.RemoteHTTP -> {
                    val handleDirectly = ConfigurationSettings.intentHandlingOption.data == IntentHandlingOptions.WithRecognition
                    val intent = HttpService.intentRecognition(text, handleDirectly)

                    if (!handleDirectly && ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                        intent?.also {
                            intentRecognized(it)
                        } ?: run {
                            intentNotRecognized()
                        }
                    }
                }
                IntentRecognitionOptions.RemoteMQTT -> MqttService.intentRecognition(text)
                IntentRecognitionOptions.Disabled -> logger.d { "intentRecognition disabled" }
            }
        }
    }

    /**
     * hermes/intent/<intentName>
     * Sent when an intent was successfully recognized
     *
     * Response to hermes/nlu/query
     *
     * Only does something if intent handling is enabled
     *
     * HomeAssistant:
     * TODO
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
    fun intentRecognized(intent: String) {
        isIntentRecognized = true

        if (AppSettings.isIntentHandlingEnabled.data) {
            coroutineScope.launch {
                when (ConfigurationSettings.intentHandlingOption.data) {
                    IntentHandlingOptions.HomeAssistant -> HomeAssistantService.sendIntent(intent)
                    IntentHandlingOptions.RemoteHTTP -> HttpService.intentHandling(intent)
                    IntentHandlingOptions.WithRecognition -> logger.v { "intentHandling with recognition was used" }
                    IntentHandlingOptions.Disabled -> logger.d { "intentHandling disabled" }
                }
            }
        }

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            endSession(currentSessionId.value)
        }
    }

    /**
     * hermes/nlu/intentNotRecognized
     * Sent when intent recognition fails
     *
     * Response to hermes/nlu/query
     *
     * if local dialogue management it will end the session
     */
    fun intentNotRecognized() {
        isIntentRecognized = false
        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            endSession(currentSessionId.value)
        }
    }


    /**
     * hermes/hanlde/toggleOn
     * Enables intent handling
     *
     * hermes/hanlde/toggleOff
     * Disables intent handling
     */
    fun intentHandlingToggle(onOff: Boolean) {
        AppSettings.isIntentHandlingEnabled.data = onOff
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
        coroutineScope.launch {

            when (ConfigurationSettings.textToSpeechOption.data) {
                TextToSpeechOptions.RemoteHTTP -> {
                    HttpService.textToSpeech(text)?.also {
                        playAudio(it)
                    }
                }
                TextToSpeechOptions.RemoteMQTT -> MqttService.say(text)
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
    fun playAudio(data: ByteArray) {

        if (AppSettings.isAudioOutputEnabled.data) {
            coroutineScope.launch {
                when (ConfigurationSettings.audioPlayingOption.data) {
                    AudioPlayingOptions.Local -> AudioPlayer.playData(data)
                    AudioPlayingOptions.RemoteHTTP -> HttpService.playWav(data)
                    AudioPlayingOptions.RemoteMQTT -> MqttService.playBytes(data)
                    AudioPlayingOptions.Disabled -> logger.d { "audioPlaying disabled" }
                }
            }
        }

    }

    /**
     * hermes/audioServer/toggleOff
     * Disable audio output
     *
     * hermes/audioServer/toggleOn
     * Enable audio output
     */
    fun audioServerToggle(onOff: Boolean) {
        AppSettings.isAudioOutputEnabled.data = onOff
    }

    /**
     * hermes/audioServer/<siteId>/playFinished
     * Audio has finished playing
     */
    fun playFinished() {
        MqttService.playFinished()
    }

    /**
     * rhasspy/audioServer/setVolume
     * Set the volume at one or more sites
     */
    fun setVolume(volume: Float) {
        AppSettings.volume.data = volume
    }

    /**
     * Speech to Text (Wav Data)
     * used to translate last spoken
     *
     * HTTP:
     * - calls service to translate speech to text, then handles the intent if dialogue manager is set to local
     *
     * RemoteMQTT
     * - audio was already send to mqtt while recording in [audioFrame]
     */
    private fun speechToText() {
        coroutineScope.launch {

            when (ConfigurationSettings.speechToTextOption.data) {
                //wait for finish -> then publish all
                SpeechToTextOptions.RemoteHTTP -> HttpService.speechToText(ServiceInterface.getLatestRecording())
                SpeechToTextOptions.RemoteMQTT -> logger.d { "speechToText already send to mqtt" }
                SpeechToTextOptions.Disabled -> logger.d { "speechToText disabled" }
            }
        }
    }

}