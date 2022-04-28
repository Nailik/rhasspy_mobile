package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import dev.icerock.moko.mvvm.livedata.readOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.services.RecordingService.addWavHeader
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.services.native.FileWriter
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.services.native.NativeLocalWakeWordService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.GlobalData
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ServiceInterface {
    private val logger = Logger.withTag("ServiceInterface")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //toggle on off from mqtt or http service
    private var isIntentRecognized = false

    // private var previousRecording = listOf<Byte>()
    private var currentRecording = mutableListOf<Byte>()

    private var mqttSpeechToTextSessionId: String? = null
    private val previousRecordingFile = FileWriter("previousRecording.wav", 0)

    private var liveSessionRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    private var liveIsPlayingRecording: MutableLiveData<Boolean> = MutableLiveData(false)
    private var liveIsRestarting: MutableLiveData<Boolean> = MutableLiveData(false)

    private var isSendAudioCaptured = false
    var sessionRunning = liveSessionRunning.readOnly()
    var isPlayingRecording = liveIsPlayingRecording.readOnly()
    var isRestarting = liveIsRestarting.readOnly()


    private var currentlyRestarting: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                liveIsRestarting.postValue(value)
            }
        }


    private var currentlyPlayingRecording: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                liveIsPlayingRecording.postValue(value)
            }
        }

    /**
     * not null if there is currently a session
     */
    private var currentSessionId: String? = null
        set(value) {
            if (field != value) {
                field = value
                liveSessionRunning.postValue(value != null)
            }
        }

    /**
     * hermes/dialogueManager/startSession
     * Starts a new dialogue session (done automatically on hotword detected)
     * only handeled when DialogueManagement is local
     *
     * Response(s)
     * hermes/dialogueManager/sessionStarted
     */
    fun startSession() {
        logger.d { "startSession" }

        //start session only if there is not one running at the moment
        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local && currentSessionId == null) {
            val sessionUuid = uuid4().toString()
            //send response
            MqttService.sessionStarted(sessionUuid)
            sessionStarted(sessionUuid)
        }
    }

    /**
     * hermes/dialogueManager/endSession
     * Requests that a session be terminated nominally
     */
    fun endSession(sessionId: String? = currentSessionId) {
        logger.d { "endSession $sessionId" }

        if (sessionId != currentSessionId) {
            logger.e { "endSession with invalid id" }
            return
        }

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            //finish the session
            sessionEnded(sessionId)
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
    fun sessionStarted(sessionId: String, fromMQTT: Boolean = false) {
        logger.d { "sessionStarted $sessionId mqtt $fromMQTT" }

        if (currentSessionId != null &&
            !(fromMQTT && ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.RemoteMQTT)
        ) {
            logger.e { "sessionStarted with invalid id" }
            return
        }

        //allow internal call or when dialog option is mqtt
        if (!fromMQTT || ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.RemoteMQTT) {

            //reset intent recognized
            isIntentRecognized = false


            //set session id
            currentSessionId = sessionId

            //when there is local dialogue management we need to stop hotWord and start listening for speech
            if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                hotWordToggle(false)

                //  if(ConfigurationSettings.speechToTextOption.data != SpeechToTextOptions.RemoteMQTT) {
                //when mqtt is used for speech to text it will automatically call startListening with a new sessionId
                startListening(sessionId)
                //    }
            }
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
    fun sessionEnded(sessionId: String?, fromMQTT: Boolean = false) {
        logger.d { "sessionEnded $sessionId mqtt $fromMQTT" }

        if (sessionId != currentSessionId) {
            logger.e { "sessionEnded with invalid id" }
            return
        }

        //allow internal call or when dialog option is mqtt
        if (!fromMQTT || ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.RemoteMQTT) {

            //reset session id
            currentSessionId = null
            //send session ended

            if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                //stop listening will remove indication and stop recording
                stopListening()
                //send that session has ended
                MqttService.sessionEnded(sessionId)
                //after session end, send that no intent was recognized if none was recognized
                if (!isIntentRecognized) {
                    MqttService.intentNotRecognized(sessionId)
                }
            }
        }
    }

    /**
     * hermes/audioServer/<siteId>/audioFrame
     * WAV chunk from microphone
     */
    fun audioFrame(byteData: List<Byte>) {
        coroutineScope.launch {
            if (!currentlyPlayingRecording) {
                if (AppSettings.isLogAudioFrames.data) {
                    logger.d { "audioFrame ${byteData.size}" }
                }

                val dataWithHeader = byteData.addWavHeader()

                if (currentSessionId == null) {
                    if (AppSettings.isHotWordEnabled.data) {
                        //current session is running
                        //no current session running
                        if (ConfigurationSettings.isUDPOutput.data) {
                            //send to udp if udp streaming only outside asr listening
                            UdpService.streamAudio(dataWithHeader)
                        } else if (ConfigurationSettings.wakeWordOption.data == WakeWordOption.MQTT) {
                            //send to mqtt for wake word detection
                            MqttService.audioFrame(dataWithHeader)
                        }
                    }
                } else {
                    //add audio to current recording for intent recognition and replay
                    currentRecording.addAll(byteData)

                    if (ConfigurationSettings.speechToTextOption.data == SpeechToTextOptions.RemoteMQTT) {
                        //send to mqtt for speech to text
                        MqttService.audioFrame(dataWithHeader)
                    }
                }
            }
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
        logger.d { "hotWordToggle $onOff" }

        //save hotWord settings
        AppSettings.isHotWordEnabled.data = onOff

        //start or stop (service and recording)
        if (onOff) {
            startHotWord()
        } else {
            stopHotWord()
        }
    }

    /**
     * hermes/wake/hotword/<wakewordId>/detected
     * Indicates a hotWord was successfully detected
     *
     * used if user clicks on record or local service detected hot word
     * or remote mqtt service detects hotWord
     */
    fun hotWordDetected(fromMQTT: Boolean = false) {
        logger.d { "hotWordDetected" }

        if (!fromMQTT) {
            //send to mqtt that hotWord was detected
            MqttService.hotWordDetected()
        }

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            //start the session
            startSession()
        }
    }

    /**
     * hermes/error/hotword
     * Sent when an error occurs in the hotWord system
     *
     * used when there is an error in the porcupine system
     */
    private fun hotWordError(description: String) {
        logger.d { "hotWordError $description" }
        //send that there was an error in hotWord system
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
    fun startListening(sessionId: String? = currentSessionId, fromMQTT: Boolean = false, sendAudioCaptured: Boolean = false) {
        logger.d { "startListening $sessionId mqtt $fromMQTT" }

        if (currentSessionId != sessionId &&
            fromMQTT && ConfigurationSettings.speechToTextOption.data == SpeechToTextOptions.RemoteMQTT
        ) {
            //store the mqtt session id to understand the id when the text was captured
            mqttSpeechToTextSessionId = sessionId
        }

        if (sessionId != currentSessionId) {
            logger.e { "startListening with invalid id" }
            return
        }

        //allow internal call or when dialog option is mqtt
        //also directly called after wake word detection
        if (!fromMQTT || ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.RemoteMQTT) {

            isSendAudioCaptured = sendAudioCaptured

            //clear current recording and start
            currentRecording.clear()
            RecordingService.startRecording()
            //show indication so user knows recording has startd
            indication(true)

            //only necessary when local dialog management
            if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local &&
                ConfigurationSettings.speechToTextOption.data == SpeechToTextOptions.RemoteMQTT
            ) {
                //tell asr system to start listening and transcribe text when mqtt is used for speech to text
                MqttService.startListening(currentSessionId)
            }
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
    fun stopListening(sessionId: String? = currentSessionId, fromMQTT: Boolean = false) {

        logger.d { "stopListening $sessionId mqtt $fromMQTT" }

        if (sessionId != currentSessionId) {
            logger.e { "stopListening with invalid id" }
            return
        }

        //allow internal call or when dialog option is mqtt
        if (!fromMQTT || ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.RemoteMQTT) {

            if (ConfigurationSettings.wakeWordOption.data != WakeWordOption.MQTT) {
                //only stop recording if its not necessary for mqtt wakeWord
                RecordingService.stopRecording()
            }

            playRecordedSound()

            //hide the indication
            indication(false)

            //copy current recording to a file
            if (currentRecording.isNotEmpty()) {
                //may be empty because stop Listening is called twice
                val previousAudioData = currentRecording.addWavHeader()
                previousRecordingFile.writeData(previousAudioData)

                if (isSendAudioCaptured) {
                    MqttService.audioCaptured(sessionId, previousAudioData)
                }

                //clear current Recording
                currentRecording.clear()
            }

            //when local dialogue management it's necessary to turn on hotWord again and transcribe the speech to text
            //only if there is a running session, maybe recording was started external
            if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local && currentSessionId != null) {
                hotWordToggle(true)
                speechToText()
            }

            //when mqtt is used for speech to text, the service needs to know that no more frames are coming
            if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local &&
                ConfigurationSettings.speechToTextOption.data == SpeechToTextOptions.RemoteMQTT
            ) {
                //tell asr system to start listening and transcribe text when mqtt is used for speech to text
                MqttService.stopListening(currentSessionId)
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

        logger.d { "asrTextCaptured $sessionId text $text" }

        //sessionId can also be mqttSpeechToTextSessionId when this is used for text to speech
        if (sessionId == null || (sessionId != currentSessionId && sessionId != mqttSpeechToTextSessionId)) {
            logger.e { "asrTextCaptured with invalid id" }
            return
        }

        //reset mqttSpeechToTextSessionId
        mqttSpeechToTextSessionId = null

        //when speech to text mqtt is used and local dialogue managment then try to recognize intent from text
        if (ConfigurationSettings.speechToTextOption.data == SpeechToTextOptions.RemoteMQTT &&
            ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local
        ) {
            //only try to recognize intent with local dialogue management
            text?.also {
                recognizeIntent(it)
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

        logger.d { "asrError $sessionId" }

        if (sessionId != currentSessionId) {
            logger.e { "asrError with invalid id" }
            return
        }

        playErrorSound()

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            //stop listening and end the session after asr error
            stopListening()
            endSession(currentSessionId)
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

        logger.d { "recognizeIntent $text" }

        coroutineScope.launch {
            when (ConfigurationSettings.intentRecognitionOption.data) {
                IntentRecognitionOptions.RemoteHTTP -> {
                    //get intent from http endpoint
                    val handleDirectly = ConfigurationSettings.intentHandlingOption.data == IntentHandlingOptions.WithRecognition
                    val intent = HttpService.intentRecognition(text, handleDirectly)

                    if (!handleDirectly && ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                        //if intent wasn't already handled and local dialogue management, handle it
                        intent?.also {
                            intentRecognized(intent = it)
                        } ?: run {
                            intentNotRecognized()
                        }
                    }

                    if (handleDirectly && ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                        //if intent was handled directly and local dialogue management it's time to end dialogue
                        endSession()
                    }
                }
                //send intent to mqtt service
                IntentRecognitionOptions.RemoteMQTT -> MqttService.intentQuery(currentSessionId, text)
                IntentRecognitionOptions.Disabled -> {
                    logger.d { "intentRecognition disabled" }
                    if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                        intentNotRecognized()
                    }

                }
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
    fun intentRecognized(sessionId: String? = currentSessionId, intent: String) {

        logger.d { "intentRecognized $sessionId intent $intent" }

        if (sessionId != currentSessionId) {
            logger.e { "intentRecognized with invalid id" }
            return
        }

        //save that intent was recognized
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
            //end the session when dialogue management is local
            endSession(sessionId)
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
    fun intentNotRecognized(sessionId: String? = currentSessionId) {

        logger.d { "intentNotRecognized $sessionId" }

        if (sessionId != currentSessionId) {
            logger.e { "intentNotRecognized with invalid id" }
            return
        }

        //save that intent was not recognized
        isIntentRecognized = false

        playErrorSound()

        if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
            //end the session when dialogue management is local
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
        logger.d { "intentHandlingToggle $onOff" }
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
        logger.d { "say $text" }

        coroutineScope.launch {
            when (ConfigurationSettings.textToSpeechOption.data) {
                TextToSpeechOptions.RemoteHTTP -> {
                    //use remote text to speech to get audio data and then play it
                    HttpService.textToSpeech(text)?.also {
                        playAudio(it)
                    }
                }
                //when mqtt is used, say will published and automatically playBytes will be invoked on this siteId
                TextToSpeechOptions.RemoteMQTT -> MqttService.say(currentSessionId, text)
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
        logger.d { "audioServerToggle $onOff" }
        AppSettings.isAudioOutputEnabled.data = onOff
    }

    /**
     * hermes/audioServer/<siteId>/playFinished
     * Audio has finished playing
     */
    fun playFinished() {
        logger.d { "playFinished" }
        currentlyPlayingRecording = false
        if (!currentlyPlayingRecording) {
            MqttService.playFinished()
        }
    }

    /**
     * rhasspy/audioServer/setVolume
     * Set the volume at one or more sites
     */
    fun setVolume(volume: Float) {
        logger.d { "setVolume $volume" }
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
        logger.d { "speechToText" }

        coroutineScope.launch {

            when (ConfigurationSettings.speechToTextOption.data) {
                //send the recording to the http endpoint
                SpeechToTextOptions.RemoteHTTP -> HttpService.speechToText(getPreviousRecording())?.also {
                    MqttService.asrTextCaptured(currentSessionId, it)
                    if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                        //when dialogue management is local endpoint, try to recognize it
                        recognizeIntent(it)
                    }
                } ?: run {
                    MqttService.asrError(currentSessionId)
                }
                //when speech to text mqtt is used, then speech is send in chunks while recording
                SpeechToTextOptions.RemoteMQTT -> logger.v { "speechToText already send to mqtt" }
                SpeechToTextOptions.Disabled -> if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                    //when dialogue management is local go to intentNotRecognized because there is no speech to text happening
                    intentNotRecognized()
                }
            }
        }
    }


    /**
     * Start services according to settings
     */
    suspend fun serviceAction(serviceAction: ServiceAction) {
        logger.d { "serviceAction ${serviceAction.name}" }

        when (serviceAction) {
            ServiceAction.Start -> {
                UdpService.start()
                startHotWord()
                HttpServer.start()
                MqttService.start()
            }
            ServiceAction.Stop -> {
                //reset values
                isSendAudioCaptured = false
                currentlyPlayingRecording = false
                mqttSpeechToTextSessionId = null
                currentRecording.clear()
                isIntentRecognized = false
                currentSessionId = null

                UdpService.stop()
                stopHotWord()
                HttpServer.stop()
                MqttService.stop()
            }
            ServiceAction.Reload -> {
                currentlyRestarting = true
                serviceAction(ServiceAction.Stop)
                serviceAction(ServiceAction.Start)
                currentlyRestarting = false
            }
        }
    }

    /**
     * call the native indication and show/hide necessary indications
     */
    private fun indication(show: Boolean) {
        logger.d { "toggle indication show: $show" }

        if (show) {
            if (AppSettings.isWakeWordSoundIndication.data) {
                playWakeSound()
            }

            if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
                NativeIndication.wakeUpScreen()
            }

            if (AppSettings.isWakeWordLightIndication.data) {
                NativeIndication.showIndication()
            }
        } else {
            NativeIndication.closeIndicationOverOtherApps()
            NativeIndication.releaseWakeUp()
        }
    }


    /**
     * start hotWord services
     */
    private fun startHotWord() {
        logger.d { "startHotWord" }

        when (ConfigurationSettings.wakeWordOption.data) {
            WakeWordOption.Porcupine -> {
                //when porcupine is used for hotWord then start local service
                if (ConfigurationSettings.wakeWordPorcupineAccessToken.data.isNotEmpty()) {
                    NativeLocalWakeWordService.start()
                } else {
                    val description = "couldn't start local wake word service, access Token Empty"
                    hotWordError(description)
                    logger.e { description }
                }
            }
            //when mqtt is used for hotWord, start recording
            WakeWordOption.MQTT -> RecordingService.startRecording()
            WakeWordOption.Disabled -> {}
        }
    }

    /**
     * stop hotWord services
     */
    private fun stopHotWord() {
        logger.d { "stopHotWord" }
        //make sure it is stopped
        NativeLocalWakeWordService.stop()
        //stop recorder if not used
        if (currentSessionId == null) {
            //if no running session then it's not necessary to record
            RecordingService.stopRecording()
        }
    }

    /**
     * when recording service detected silence
     */
    fun silenceDetected() {
        logger.d { "silenceDetected" }
        //silence Detected may be invoked during mqtt wakeWord
        if (currentSessionId != null) {
            stopListening()
        }
    }

    /**
     * used when user wants to toggle
     */
    fun toggleSession() {
        logger.d { "toggleSession" }
        currentSessionId?.also {
            stopListening()
        } ?: run {
            hotWordDetected()
        }
    }

    /**
     * plays last recording
     */
    fun playRecording() {
        logger.d { "playRecording" }
        if (!currentlyPlayingRecording) {
            currentlyPlayingRecording = true
            AudioPlayer.playData(getPreviousRecording())
        }
    }

    /**
     * Saves configuration changes
     */
    fun saveAndApplyChanges() {
        coroutineScope.launch {
            currentlyRestarting = true
            GlobalData.saveAllChanges()
            ForegroundService.action(ServiceAction.Reload)
        }
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
    fun getPreviousRecording(): List<Byte> {
        return previousRecordingFile.getFileData()
    }

    private fun playWakeSound() {
        when (AppSettings.wakeSound.data) {
            0 -> NativeIndication.playSoundFileResource(MR.files.etc_wav_beep_hi)
            1 -> {}
            else -> NativeIndication.playSoundFile(AppSettings.wakeSounds.data.elementAt(AppSettings.wakeSound.data - 2))
        }
    }

    private fun playRecordedSound() {
        when (AppSettings.recordedSound.data) {
            0 -> NativeIndication.playSoundFileResource(MR.files.etc_wav_beep_lo)
            1 -> {}
            else -> NativeIndication.playSoundFile(AppSettings.recordedSounds.data.elementAt(AppSettings.recordedSound.data - 2))
        }
    }

    private fun playErrorSound() {
        when (AppSettings.errorSound.data) {
            0 -> NativeIndication.playSoundFileResource(MR.files.etc_wav_beep_error)
            1 -> {}
            else -> NativeIndication.playSoundFile(AppSettings.errorSounds.data.elementAt(AppSettings.errorSound.data - 2))
        }
    }

}