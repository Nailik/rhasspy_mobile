package org.rhasspy.mobile.services.dialogue

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.services.*
import org.rhasspy.mobile.services.RecordingService.addWavHeader
import org.rhasspy.mobile.services.RecordingService.startRecording
import org.rhasspy.mobile.services.http.HttpServer
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.services.native.NativeLocalWakeWordService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.GlobalData
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ServiceInterface {
    private val logger = Logger.withTag(this::class.simpleName!!)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //toggle on off from mqtt or http service
    private var isIntentRecognized = false

    private var previousRecording = listOf<Byte>()
    private var currentRecording = mutableListOf<Byte>()


    private val indicationVisible = MutableLiveData(false)
    val isIndicationVisible: LiveData<Boolean> = indicationVisible.readOnly()

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
        if (currentSessionId.value != null) {
            logger.e { "received startSession but there is another current session running" }
        }

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            val sessionUuid = uuid4()
            coroutineScope.launch {
                //send response
                MqttService.sessionStarted(sessionUuid.toString())
            }
            //start the session
            sessionStarted(sessionUuid.toString())
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

        currentSessionId.value?.also { id ->

            if (!isIntentRecognized) {
                MqttService.intentNotRecognized(id)
            }

            if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                coroutineScope.launch {
                    //send response
                    MqttService.sessionEnded(id)
                }
                //start the session
                sessionEnded(id)
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
        if (currentSessionId.value != null) {
            logger.e { "received sessionStarted but there is another current session running" }
        }

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
    fun sessionEnded(sessionId: String?) {
        if (sessionId != this.currentSessionId.value) {
            logger.e { "trying to end session with invalid id" }
            return
        }

        currentSessionId.value = null

        stopListening()
    }

    /**
     * hermes/audioServer/<siteId>/audioFrame
     * WAV chunk from microphone
     */
    fun audioFrame(byteArray: ByteArray) {
        //send to udp if udp streaming
        if (AppSettings.isAudioOutputEnabled.data) {
            UdpService.streamAudio(byteArray.addWavHeader())
        }

        //send to mqtt if mqtt listen for WakeWord or mqtt text to speech
        if (ConfigurationSettings.wakeWordOption.data == WakeWordOption.MQTT && currentSessionId.value == null ||
            (ConfigurationSettings.speechToTextOption.data == SpeechToTextOptions.RemoteMQTT && currentSessionId.value != null)
        ) {
            MqttService.audioFrame(byteArray.addWavHeader())
        }

        //if there is a current session record this audio to save if for intent recognition
        if (currentSessionId.value != null) {
            currentRecording.addAll(byteArray.toList())
        }
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

        if (onOff) {
            startHotWord()
        } else {
            stopHotWord()
        }
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
    private fun hotWordError(description: String) {
        MqttService.hotWordError(description)
    }

    /**
     * hermes/asr/startListening
     * Tell ASR system to start recording/transcribing
     *
     * used to start recording
     * resets current recording
     *
     * when mqtt speech to text is set, it's necessary to tell the asr system to start listening
     */
    fun startListening() {
        //only start listening if not currently recording
        //this can loop because it calls the mqtt service to start transcribing but also receives this message
        indication(true)
        if (!RecordingService.status.value) {
            currentRecording.clear()
            startRecording()

            if (ConfigurationSettings.speechToTextOption.data == SpeechToTextOptions.RemoteMQTT) {
                MqttService.startListening(currentSessionId.value)
            }
        } else {
            logger.e { "already listening" }
        }
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
     * sendToMqtt is used to not set stopListening to MQTT asr system, when the asr response called this function
     */
    fun stopListening(sessionId: String? = currentSessionId.value, sendToMqtt: Boolean = true) {
        if (sessionId == currentSessionId.value) {
            indication(false)

            if (RecordingService.status.value) {
                if(ConfigurationSettings.wakeWordOption.data != WakeWordOption.MQTT) {
                    //only stop recording if its not necessary for mqtt wakeWord
                    RecordingService.stopRecording()
                }
                //independent copy of current recording
                previousRecording = mutableListOf<Byte>().apply { addAll(currentRecording) }

                if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                    hotWordToggle(true)
                    speechToText()
                    if (ConfigurationSettings.speechToTextOption.data == SpeechToTextOptions.RemoteMQTT && sendToMqtt) {
                        MqttService.stopListening()
                    }
                }
            }
        }
    }

    /**
     * hermes/asr/textCaptured
     * Successful transcription, sent either when silence is detected or on stopListening
     *
     * listening will be stopped
     * if local dialogue management it will try to recognize intent from text
     */
    fun asrTextCaptured(sessionId: String?, text: String?) {
        if (sessionId == currentSessionId.value) {
            stopListening(sendToMqtt = false)

            if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                stopListening(sessionId)
                text?.also {
                    recognizeIntent(it)
                }
            }
        }
    }

    /**
     * hermes/error/asr
     * Sent when an error occurs in the ASR system
     *
     * listening will be stopped
     * if local dialogue management it will end the session
     */
    fun asrError(sessionId: String?) {
        if (sessionId == currentSessionId.value) {
            if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                stopListening(sendToMqtt = false)
                endSession(currentSessionId.value)
            } else {
                stopListening(sendToMqtt = false)
            }
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
     * - later [intentRecognized] or [intentNotRecognized] will be called with received data
     *
     * MQTT:
     * - calls default site to recognize intent
     * - later eventually [intentRecognized] or [intentNotRecognized] will be called with received data
     */
    fun recognizeIntent(text: String) {
        coroutineScope.launch {
            when (ConfigurationSettings.intentRecognitionOption.data) {
                IntentRecognitionOptions.RemoteHTTP -> {
                    val handleDirectly = ConfigurationSettings.intentHandlingOption.data == IntentHandlingOptions.WithRecognition
                    val intent = HttpService.intentRecognition(text, handleDirectly)

                    if (!handleDirectly && ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                        CoroutineScope(Dispatchers.Main).launch {
                            intent?.also {
                                intentRecognized(intent = it)
                            } ?: run {
                                intentNotRecognized()
                            }
                        }
                    }
                }
                IntentRecognitionOptions.RemoteMQTT -> MqttService.intentQuery(currentSessionId.value, text)
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
    fun intentRecognized(sessionId: String? = currentSessionId.value, intent: String) {
        if (sessionId == currentSessionId.value) {
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
                endSession(sessionId)
            }
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
    fun intentNotRecognized(sessionId: String? = currentSessionId.value) {
        isIntentRecognized = false
        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            endSession(sessionId)
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
                TextToSpeechOptions.RemoteMQTT -> MqttService.say(currentSessionId.value, text)
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
                SpeechToTextOptions.RemoteHTTP -> HttpService.speechToText(getPreviousRecording())
                SpeechToTextOptions.RemoteMQTT -> logger.d { "speechToText already send to mqtt" }
                SpeechToTextOptions.Disabled -> logger.d { "speechToText disabled" }
            }
        }
    }


    /**
     * Start services according to settings
     */
    fun serviceAction(serviceAction: ServiceAction) {
        logger.d { "serviceAction ${serviceAction.name}" }

        when (serviceAction) {
            ServiceAction.Start -> {
                startHotWord()
                HttpServer.start()
                MqttService.start()
            }
            ServiceAction.Stop -> {
                stopHotWord()
                HttpServer.stop()
                MqttService.stop()
            }
            ServiceAction.Reload -> {
                serviceAction(ServiceAction.Stop)
                serviceAction(ServiceAction.Start)
            }
        }
    }

    /**
     * call the native indication and show/hide necessary indications
     */
    private fun indication(show: Boolean) {
        logger.d { "toggle indication show: $show" }

        if(indicationVisible.value != show) {
            if (show) {
                indicationVisible.value = true
                if (AppSettings.isWakeWordSoundIndication.data) {
                    NativeIndication.playAudio(MR.files.etc_wav_beep_hi)
                }

                if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
                    NativeIndication.wakeUpScreen()
                }

                if (AppSettings.isWakeWordLightIndication.data) {
                    NativeIndication.showIndication()
                }
            } else {
                indicationVisible.value = false
                NativeIndication.closeIndicationOverOtherApps()
                NativeIndication.releaseWakeUp()
            }
        }
    }


    /**
     * starts the local wakeword Service
     */
    private fun startHotWord() {
        when (ConfigurationSettings.wakeWordOption.data) {
            WakeWordOption.Porcupine -> {
                if (ConfigurationSettings.wakeWordAccessToken.data.isNotEmpty()) {
                    NativeLocalWakeWordService.start()
                } else {
                    val description = "couldn't start local wake word service, access Token Empty"
                    hotWordError(description)
                    logger.e { description }
                }
            }
            //necessary to continuously stream audio
            WakeWordOption.MQTT -> startRecording()
            WakeWordOption.Disabled -> {}
        }
    }

    private fun stopHotWord() {
        when (ConfigurationSettings.wakeWordOption.data) {
            WakeWordOption.Porcupine -> NativeLocalWakeWordService.stop()
            //nothing to do, audio frames wont be sent to broker
            WakeWordOption.MQTT -> {}
            WakeWordOption.Disabled -> {}
        }
    }

    fun silenceDetected() {
        stopListening()
    }

    /**
     * starts or stops a session, depends if it is currently running
     */
    fun toggleSession() {
        sessionId.value?.also {
            endSession(it)
        } ?: run {
            startSession()
        }
    }

    /**
     * plays last recording
     */
    fun playRecording() {
        AudioPlayer.playData(getPreviousRecording())
    }

    /**
     * Saves configuration changes
     */
    fun saveAndApplyChanges() {
        GlobalData.saveAllChanges()
        ForegroundService.action(ServiceAction.Reload)
    }

    /**
     * resets configuration changes
     */
    fun resetChanges() {
        GlobalData.resetChanges()
    }

    /**
     * returns previouw Recording as wav data
     */
    fun getPreviousRecording(): ByteArray {
        return previousRecording.toByteArray().addWavHeader()
    }

}