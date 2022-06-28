package org.rhasspy.mobile.logic

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.addWavHeader
import org.rhasspy.mobile.data.DialogueManagementOptions
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.nativeutils.AudioPlayer
import org.rhasspy.mobile.nativeutils.FileWriter
import org.rhasspy.mobile.observer.MutableObservable
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.RhasspyActions
import org.rhasspy.mobile.services.UdpService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object StateMachine {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val logger = Logger.withTag("StateMachine")

    private var previousRecordingFile = FileWriter("previousRecording.wav", 0)

    //saves data about current session, dummy initial value
    var currentSession: Session = Session("", "")
        private set

    //information about current state
    private var state = MutableObservable(State.Stopped)
    val currentState = state.readOnly()

    /**
     * indicates that services have started
     * resets session data and state to AwaitingHotWord
     */
    fun started() {
        logger.v { "started" }

        if (state.value == State.Stopped) {
            currentSession = Session("", "")
            state.value = State.AwaitingHotWord
        } else {
            logger.e { "started call with invalid state ${state.value}" }
        }
    }

    /**
     * services stopped
     */
    fun stopped() {
        logger.v { "stopped" }

        state.value = State.Stopped
    }

    /**
     * when a hot word was detected, either by clicking on the icon,
     * when remotely a hotWord was detected or when the internal wake word services triggered
     * keyword indicates which keyword triggered it
     * starts the session
     *
     * hermes/wake/hotword/<wakewordId>/detected
     * Indicates a hotWord was successfully detected
     */
    fun hotWordDetected(keyword: String) {
        logger.v { "hotWordDetected" }

        if (state.value == State.AwaitingHotWord) {
            state.value = State.StartingSession
            //send to mqtt that hotWord was detected
            MqttService.hotWordDetected(keyword)
            //call session started with a new unique id
            startedSession(uuid4().toString(), keyword)
        } else {
            logger.e { "hotWordDetected call with invalid state ${state.value}" }
        }
    }

    /**
     * hermes/error/hotword
     * Sent when an error occurs in the hotWord system
     *
     * used when there is an error in the porcupine system
     */
    fun hotWordError(description: String) {
        logger.e { "hotWordError $description" }
        //send that there was an error in hotWord system
        MqttService.hotWordError(description)
    }

    /**
     * can be used to start a session remotely instead of hot word detected
     * only works with local dialogue management
     *
     * hermes/dialogueManager/startSession
     * Starts a new dialogue session (done automatically on hotword detected)
     * only handled when DialogueManagement is local
     *
     * Response(s)
     * hermes/dialogueManager/sessionStarted
     */
    fun startSession() {
        logger.v { "startSession" }

        if (isDialogueLocal()) {
            if (state.value == State.AwaitingHotWord) {
                state.value = State.StartingSession
                //call session started with a new unique id
                startedSession(uuid4().toString(), "")
            } else {
                logger.e { "hotWordDetected call with invalid state ${state.value}" }
            }
        }
    }

    /**
     * indicates that a session has started
     * internal dialogue manager will disable hotWord and start recording now
     *
     * hermes/dialogueManager/sessionStarted
     * Indicates a session has started
     *
     * Response to [hermes/dialogueManager/startSession]
     */
    fun startedSession(sessionId: String, keyword: String) {
        logger.v { "startedSession id: $sessionId keyword: $keyword" }

        if (state.value == State.StartingSession) {
            currentSession = Session(sessionId, keyword)
            state.value = State.StartedSession
            //send to mqtt that a session has started
            MqttService.sessionStarted(sessionId)
            //start recording (listening)
            startListening()
        } else {
            logger.e { "startedSession call with invalid state ${state.value}" }
        }
    }

    /**
     * when mqtt speech to text is set, it's necessary to tell the asr system to start listening
     * resets current recording, start recording will automatically be started because of new state
     *
     * hermes/asr/startListening
     * Tell ASR system to start recording/transcribing
     */
    fun startListening(sessionId: String? = currentSession.sessionId, sendAudioCaptured: Boolean = false, fromMQTT: Boolean = false) {
        logger.v { "startListening id: $sessionId sendAudioCaptured: $sendAudioCaptured fromMQTT: $fromMQTT" }

        if (state.value == State.StartedSession) {
            if (!fromMQTT && sessionId == currentSession.sessionId) {
                //save send audio captured to session
                currentSession.isSendAudioCaptured = sendAudioCaptured
                //clear current recording
                currentSession.currentRecording.clear()
                //set state
                state.value = State.RecordingIntent

                //only necessary when local dialog management
                if (isDialogueLocal() &&
                    ConfigurationSettings.speechToTextOption.value == SpeechToTextOptions.RemoteMQTT
                ) {
                    //tell asr system to start listening and transcribe text when mqtt is used for speech to text
                    MqttService.startListening(currentSession.sessionId)
                }

            } else if (fromMQTT && ConfigurationSettings.speechToTextOption.value == SpeechToTextOptions.RemoteMQTT) {
                //save session id to later understand the id when the text was captured by mqtt
                currentSession.mqttSpeechToTextSessionId = sessionId
            } else {
                logger.d { "startListening sessionId $sessionId is different to current session ${currentSession.sessionId}" }
            }
        } else {
            logger.e { "startListening call with invalid state ${state.value}" }
        }
    }

    /**
     * When session is RecordingIntent, data will be saved to current recording
     * if speechToText is set to mqtt, it will also be send to mqtt
     *
     * when sate is awaiting hotWord, data will be send to
     * udp or mqtt according to wakeWord option and isUdpOutput
     *
     * hermes/audioServer/<siteId>/audioFrame
     * WAV chunk from microphone
     */
    fun audioFrame(byteData: List<Byte>) {
        coroutineScope.launch {

            if (AppSettings.isLogAudioFrames.value) {
                logger.d { "audioFrame ${byteData.size}" }
            }

            val dataWithHeader = byteData.toMutableList().apply {
                addWavHeader()
            }

            if (state.value == State.RecordingIntent) {
                //active session is running, save data and send to mqtt if necessary

                //add audio to current recording for intent recognition and replay
                currentSession.currentRecording.addAll(byteData)

                if (ConfigurationSettings.speechToTextOption.value == SpeechToTextOptions.RemoteMQTT) {
                    //send to mqtt for speech to text
                    MqttService.audioFrame(dataWithHeader)
                }

            } else if (state.value == State.AwaitingHotWord) {
                //awaiting hotWord, send audio to udp or mqtt according to settings
                if (AppSettings.isHotWordEnabled.value) {
                    //current session is running
                    //no current session running
                    if (ConfigurationSettings.isUDPOutput.value) {
                        //send to udp if udp streaming only outside asr listening
                        UdpService.streamAudio(dataWithHeader)
                    }
                    if (ConfigurationSettings.wakeWordOption.value == WakeWordOption.MQTT) {
                        //send to mqtt for wake word detection
                        MqttService.audioFrame(dataWithHeader)
                    }
                }
            } else {
                logger.e { "audioFrame call with invalid state ${state.value}" }
            }
        }
    }

    /**
     * stop recording will automatically happen due to state change
     * it will also try to convert speech to text
     * also called when silence was detected
     *
     * saves currentRecording to previous
     * sendToMqtt is used to not set stopListening to MQTT asr system, when the asr response called this function
     *
     * hermes/asr/stopListening
     * Tell ASR system to stop recording
     *
     * Emits textCaptured if silence was not detected earlier
     */
    fun stopListening(sessionId: String? = currentSession.sessionId, fromMQTT: Boolean = false) {
        //stop it if state is recording
        logger.v { "stopListening id: $sessionId fromMQTT: $fromMQTT" }

        if (state.value == State.RecordingIntent) {
            if (sessionId == currentSession.sessionId) {
                state.value = State.RecordingStopped

                //allow internal call or when dialog option is mqtt
                if (!fromMQTT || ConfigurationSettings.dialogueManagementOption.value == DialogueManagementOptions.RemoteMQTT) {
                    state.value = State.TranscribingIntent

                    //save recording to previous recording
                    currentSession.currentRecording.addWavHeader()
                    previousRecordingFile.writeData(currentSession.currentRecording)

                    //send audio to mqtt
                    if (currentSession.isSendAudioCaptured) {
                        MqttService.audioCaptured(sessionId, currentSession.currentRecording)
                    }

                    //when local dialogue management it's necessary to turn on hotWord again and transcribe the speech to text
                    //only if there is a running session, maybe recording was started external
                    if (isDialogueLocal()) {
                        RhasspyActions.speechToText()
                    }

                    //when mqtt is used for speech to text, the service needs to know that no more frames are coming
                    if (isDialogueLocal() &&
                        ConfigurationSettings.speechToTextOption.value == SpeechToTextOptions.RemoteMQTT
                    ) {
                        //tell asr system to start listening and transcribe text when mqtt is used for speech to text
                        MqttService.stopListening(currentSession.sessionId)
                    }

                } else {
                    logger.d { "startListening called from fromMQTT $fromMQTT but dialogManagement is set to ${ConfigurationSettings.dialogueManagementOption.data}" }
                }
            } else {
                logger.d { "startListening sessionId $sessionId is different to current session ${currentSession.sessionId}" }
            }
        } else {
            logger.e { "stopListening call with invalid state ${state.value}" }
        }
    }

    /**
     * when an intent (speech) was transcribed to a text
     * will start to recognize intent in order to handle it later
     * speech to text finished
     *
     * hermes/asr/textCaptured
     * Successful transcription, sent either when silence is detected or on stopListening
     */
    fun intentTranscribed(sessionId: String? = currentSession.sessionId, intent: String, fromMQTT: Boolean = false) {
        logger.v { "intentTranscribed $intent" }

        if (state.value == State.TranscribingIntent || state.value == State.RecordingStopped) {
            if ((fromMQTT && currentSession.mqttSpeechToTextSessionId != null && currentSession.mqttSpeechToTextSessionId == sessionId) ||
                sessionId == currentSession.sessionId
            ) {

                state.value = State.RecognizingIntent

                if (isDialogueLocal()) {
                    MqttService.asrTextCaptured(currentSession.sessionId, intent)
                    //when dialogue management is local endpoint, try to recognize intent
                    RhasspyActions.recognizeIntent(intent)
                }

            }
        } else {
            logger.e { "intentTranscribed call with invalid state ${state.value}" }
        }
    }

    /**
     * manually recognize an intent
     */
    fun manualIntentRecognition(intent: String) {
        //only if nothing is currently to be done
        if (state.value == State.AwaitingHotWord) {
            state.value = State.RecognizingIntent
            RhasspyActions.recognizeIntent(intent)
        }
    }

    /**
     * there was an error in transcription (maybe it was disabled)
     * text could not be received from speech
     * will end session
     *
     * hermes/error/asr
     * Sent when an error occurs in the ASR system
     */
    fun intentTranscriptionError(sessionId: String? = currentSession.sessionId, fromMQTT: Boolean = false) {
        logger.v { "intentTranscriptionError" }

        if (state.value == State.TranscribingIntent || state.value == State.RecordingStopped) {
            if ((fromMQTT && currentSession.mqttSpeechToTextSessionId != null && currentSession.mqttSpeechToTextSessionId == sessionId) ||
                sessionId == currentSession.sessionId
            ) {
                state.value = State.TranscribingError

                if (isDialogueLocal()) {
                    MqttService.asrError(currentSession.sessionId)
                    //when dialogue management is local end the session
                    endSession()
                }
            }
        } else {
            logger.e { "intentTranscriptionError call with invalid state ${state.value}" }
        }
    }

    /**
     * hermes/intent/<intentName>
     * Sent when an intent was successfully recognized
     *
     * Response to hermes/nlu/query
     *
     * intent was recognized will now be handled and session will be ended
     */
    fun intentRecognized(intentName: String, intent: String, sessionId: String? = currentSession.sessionId) {
        logger.v { "intentRecognized $intent" }

        if (state.value == State.RecognizingIntent) {
            if (sessionId == currentSession.sessionId) {
                state.value = State.IntentHandling

                currentSession.isIntentRecognized = true

                RhasspyActions.intentHandling(intentName, intent)

                if (isDialogueLocal()) {
                    endSession()
                }
            }
        } else {
            logger.e { "intentRecognized call with invalid state ${state.value}" }
        }
    }

    /**
     * intent was not found from text
     * ending session
     *
     * hermes/nlu/intentNotRecognized
     * Sent when intent recognition fails
     *
     * Response to hermes/nlu/query
     */
    fun intentNotRecognized(sessionId: String? = currentSession.sessionId) {
        logger.v { "intentNotRecognized" }

        if (state.value == State.RecognizingIntent) {
            if (sessionId == currentSession.sessionId) {
                state.value = State.RecognizingIntentError

                if (isDialogueLocal()) {
                    sessionEnded()
                }
            }
        } else {
            logger.e { "intentNotRecognized call with invalid state ${state.value}" }
        }
    }

    /**
     * called to end a session from remote
     * only works with local dialogue management else only sets state to SessionStopped
     * recording will be stopped if necessary
     *
     * hermes/dialogueManager/endSession
     * Requests that a session be terminated nominally
     */
    fun endSession(sessionId: String? = currentSession.sessionId) {
        logger.v { "endSession $sessionId" }

        if (state.value != State.AwaitingHotWord) {
            if (sessionId == currentSession.sessionId) {
                state.value = State.SessionStopped

                //when the dialogue management is local the session will be ended
                if (isDialogueLocal()) {
                    //finish the session
                    sessionEnded()
                }
            } else {
                logger.d { "endSession sessionId $sessionId is different to current session ${currentSession.sessionId}" }
            }
        } else {
            logger.e { "endSession call with invalid state ${state.value}" }
        }
    }

    /**
     * called when a session was ended
     * currentSession will be reset
     * sets to EndedSession while finishing up, the goes to AwaitingWakeWord
     *
     * hermes/dialogueManager/sessionEnded
     * Indicates a session has terminated
     *
     * Response to hermes/dialogueManager/endSession or other reasons for a session termination
     */
    fun sessionEnded(sessionId: String? = currentSession.sessionId) {
        logger.v { "sessionEnded" }

        if (state.value == State.TranscribingError ||
            state.value == State.IntentHandling ||
            state.value == State.RecognizingIntentError ||
            state.value == State.SessionStopped
        ) {
            //check if sessionId is correct
            if (sessionId == currentSession.sessionId) {
                state.value = State.EndedSession
                //tell mqtt that session has ended
                MqttService.sessionEnded(sessionId)
                //after session end, send that no intent was recognized if none was recognized
                if (!currentSession.isIntentRecognized) {
                    MqttService.intentNotRecognized(sessionId)
                }
                //reset session
                currentSession = Session("", "")
                //await hot word
                state.value = State.AwaitingHotWord
            } else {
                logger.d { "sessionEnded sessionId $sessionId is different to current session ${currentSession.sessionId}" }
            }
        } else {
            logger.e { "sessionEnded call with invalid state ${state.value}" }
        }
    }

    /**
     * plays indication audio and calls finished to do whatever is needed
     */
    fun playAudio(data: List<Byte>, fromMQTT: Boolean = false) {
        logger.v { "playAudio" }
        //only on idle
        if (state.value == State.AwaitingHotWord) {
            state.value = State.PlayingAudio

            AudioPlayer.playData(data) {
                state.value = State.AwaitingHotWord
                if (fromMQTT) {
                    //if call was from mqtt, send message when play has finished
                    MqttService.playFinished()
                }
            }
        }
    }

    //only start recording, do not stop it
    fun playRecording() {
        if (state.value != State.PlayingRecording) {
            togglePlayRecording()
        }
    }

    fun togglePlayRecording() {
        if (state.value == State.AwaitingHotWord) {
            logger.d { "playRecording" }
            state.value = State.PlayingRecording
            AudioPlayer.playData(getPreviousRecording()) { state.value = State.AwaitingHotWord }
        } else if (state.value == State.PlayingRecording) {
            logger.d { "stopPlayingRecording" }
            AudioPlayer.stopPlayingData()
            state.value = State.AwaitingHotWord
        }
    }

    fun toggleSessionManually() {
        if (state.value == State.AwaitingHotWord) {
            hotWordDetected("${ConfigurationSettings.siteId.value}_manual")
        } else if (state.value == State.RecordingIntent) {
            stopListening()
        }
    }

    fun getPreviousRecording(): List<Byte> {
        return previousRecordingFile.getFileData()
    }

    private fun isDialogueLocal(): Boolean = ConfigurationSettings.dialogueManagementOption.value == DialogueManagementOptions.Local
}