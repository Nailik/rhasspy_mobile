package org.rhasspy.mobile.services.dialog

import com.benasher44.uuid.uuid4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.addWavHeader
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.notNull
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.services.indication.IndicationService
import org.rhasspy.mobile.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.services.wakeword.WakeWordService
import org.rhasspy.mobile.settings.option.DialogManagementOption
import org.rhasspy.mobile.settings.option.SpeechToTextOption
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * The Dialog Manager handles the various states and goes to the next state according to the function that is called
 */
class DialogManagerService(private val isTestMode: Boolean = false) : IService() {
    private val logger = LogType.DialogManagerService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    val serviceState = _serviceState.readOnly

    private val params by inject<DialogManagerServiceParams>()
    private val wakeWordService by inject<WakeWordService>()
    private val intentRecognitionService by inject<IntentRecognitionService>()
    private val intentHandlingService by inject<IntentHandlingService>()
    private val audioPlayingService by inject<AudioPlayingService>()
    private val speechToTextService by inject<SpeechToTextService>()
    private val indicationService by inject<IndicationService>()
    private val mqttService by inject<MqttService>()

    private var sessionId: String? = null
    private var sendAudioCaptured = false
    private var coroutineScope = CoroutineScope(Dispatchers.Default)

    private var timeoutJob: Job? = null

    private val textAsrTimeout = 10000.toDuration(DurationUnit.MILLISECONDS)
    private val intentRecognitionTimeout = 10000.toDuration(DurationUnit.MILLISECONDS)
    private val recordingTimeout = 100000.toDuration(DurationUnit.MILLISECONDS)

    private val _currentDialogState = MutableStateFlow(DialogManagerServiceState.Idle)
    val currentDialogState = _currentDialogState.readOnly

    override fun onClose() {
        logger.d { "onClose" }
        timeoutJob?.cancel()
        timeoutJob = null
        coroutineScope.cancel()
    }

    init {
        _serviceState.value = ServiceState.Success
        _currentDialogState.value = DialogManagerServiceState.AwaitingWakeWord
        wakeWordService.startDetection()
    }

    fun onAction(action: DialogAction) {
        logger.d { "onAction $action" }
        coroutineScope.launch {
            when (action) {
                is DialogAction.AsrError -> asrError(action)
                is DialogAction.AsrTextCaptured -> asrTextCaptured(action)
                is DialogAction.EndSession -> endSession(action)
                is DialogAction.WakeWordDetected -> wakeWordDetected(action)
                is DialogAction.IntentRecognitionResult -> intentRecognitionResult(action)
                is DialogAction.IntentRecognitionError -> intentRecognitionError(action)
                is DialogAction.PlayAudio -> playAudio(action)
                is DialogAction.StopAudioPlaying -> stopPlayAudio(action)
                is DialogAction.PlayFinished -> playFinished(action)
                is DialogAction.SessionEnded -> sessionEnded(action)
                is DialogAction.SessionStarted -> sessionStarted(action)
                is DialogAction.SilenceDetected -> silenceDetected(action)
                is DialogAction.StartListening -> startListening(action)
                is DialogAction.StartSession -> startSession(action)
                is DialogAction.StopListening -> stopListening(action)
            }
        }
    }

    /**
     * Asr Error occurs, when the speech could not be translated to text, this will result in a failed dialog
     *
     * plays error sound
     * ends session
     */
    private suspend fun asrError(action: DialogAction.AsrError) {
        if (isInCorrectState(action, DialogManagerServiceState.TranscribingIntent)) {

            timeoutJob?.cancel()
            indicationService.onError()
            informMqtt(action)
            onAction(DialogAction.SessionEnded(Source.Local))

        }
    }


    /**
     * the speech could be translated to text
     *
     * next step is to translate the text to an intent
     */
    private suspend fun asrTextCaptured(action: DialogAction.AsrTextCaptured) {
        if (isInCorrectState(action, DialogManagerServiceState.TranscribingIntent)) {

            timeoutJob?.cancel()
            _currentDialogState.value = DialogManagerServiceState.RecognizingIntent
            indicationService.onThinking()
            informMqtt(action)

            notNull(sessionId, action.text, { id, text ->
                intentRecognitionService.recognizeIntent(id, text)
                //await intent recognition
                timeoutJob = coroutineScope.launch {
                    delay(intentRecognitionTimeout)
                    logger.d { "intentRecognitionTimeout" }
                    onAction(DialogAction.IntentRecognitionError(Source.Local))
                }
            }, {
                logger.d { "asrTextCaptured parameter issue sessionId: $sessionId action.text: ${action.text}" }
                onAction(DialogAction.IntentRecognitionError(Source.Local))
            })

        }
    }

    /**
     * end the session nominally after handling intent
     *
     * next step is to invoke ended session which will start a new one
     */
    private fun endSession(action: DialogAction.EndSession) {
        if (isInCorrectState(action, DialogManagerServiceState.HandlingIntent, DialogManagerServiceState.TranscribingIntent, DialogManagerServiceState.RecognizingIntent)) {

            onAction(DialogAction.SessionEnded(Source.Local))

        }
    }

    /**
     * called when a hotWord was detected either by mqtt, local or via http api
     *
     * starts a session
     */
    private suspend fun wakeWordDetected(action: DialogAction.WakeWordDetected) {
        if (isInCorrectState(action, DialogManagerServiceState.AwaitingWakeWord)) {

            indicationService.onWakeWordDetected()
            _currentDialogState.value = DialogManagerServiceState.Idle
            informMqtt(action)
            onAction(DialogAction.StartSession(Source.Local))

        }
    }

    /**
     * intent was recognized from text
     *
     * next step is to handle this intent
     */
    private suspend fun intentRecognitionResult(action: DialogAction.IntentRecognitionResult) {
        if (isInCorrectState(action, DialogManagerServiceState.RecognizingIntent)) {

            timeoutJob?.cancel()
            intentHandlingService.intentHandling(action.intentName, action.intent)
            onAction(DialogAction.EndSession(Source.Local))

        }
    }

    private suspend fun intentRecognitionError(action: DialogAction.IntentRecognitionError) {
        if (isInCorrectState(action, DialogManagerServiceState.RecognizingIntent)) {

            timeoutJob?.cancel()
            informMqtt(action)
            indicationService.onError()
            onAction(DialogAction.SessionEnded(Source.Local))

        }
    }

    /**
     * play audio was invoked, probably playing speech
     *
     * stop the hot word detection
     * play the audio
     */
    private suspend fun playAudio(action: DialogAction.PlayAudio) {
        if (isInCorrectState(action, DialogManagerServiceState.HandlingIntent, DialogManagerServiceState.Idle, DialogManagerServiceState.AwaitingWakeWord)) {

            _currentDialogState.value = DialogManagerServiceState.PlayingAudio
            wakeWordService.stopDetection()
            indicationService.onPlayAudio()
            audioPlayingService.playAudio(action.byteArray.toMutableList().addWavHeader().toList())

        }
    }

    /**
     * stops to play the current audio
     */
    private fun stopPlayAudio(action: DialogAction.StopAudioPlaying) {
        if (isInCorrectState(action, DialogManagerServiceState.PlayingAudio)) {

            audioPlayingService.stopPlayAudio()
            onAction(DialogAction.PlayFinished(Source.Local))

        }
    }

    /**
     * playing audio finished
     *
     * go back to awaiting hotword
     * start hot word service
     * tell mqtt (if source is not mqtt)
     */
    private suspend fun playFinished(action: DialogAction.PlayFinished) {
        if (isInCorrectState(action, DialogManagerServiceState.PlayingAudio)) {

            indicationService.onIdle()
            _currentDialogState.value = DialogManagerServiceState.AwaitingWakeWord
            informMqtt(action)
            wakeWordService.startDetection()

        }
    }

    /**
     * when a session has ended
     *
     * start hotWord service again
     * await hotWord
     * tell mqtt
     */
    private suspend fun sessionEnded(action: DialogAction.SessionEnded) {
        if (isInCorrectState(action, DialogManagerServiceState.TranscribingIntent, DialogManagerServiceState.HandlingIntent, DialogManagerServiceState.RecognizingIntent)) {

            indicationService.onIdle()
            sessionId = null
            _currentDialogState.value = DialogManagerServiceState.AwaitingWakeWord
            informMqtt(action)
            wakeWordService.startDetection()

        }
    }

    /**
     * indicates that a session has started
     *
     * saves the session id
     * sends info to mqtt
     * starts recording
     */
    private suspend fun sessionStarted(action: DialogAction.SessionStarted) {
        if (isInCorrectState(action, DialogManagerServiceState.Idle)) {

            val newSessionId = when (action.source) {
                Source.HttpApi -> null
                Source.Local -> uuid4().toString()
                is Source.Mqtt -> action.source.sessionId
            }

            if (newSessionId != null) {
                sessionId = newSessionId
                informMqtt(action)
                onAction(DialogAction.StartListening(Source.Local, false))
            } else {
                logger.d { "sessionStarted parameter issue sessionId: $sessionId" }
            }

        }
    }

    /**
     * when silence is detected
     *
     * stop listening action
     */
    private fun silenceDetected(action: DialogAction.SilenceDetected) {
        if (isInCorrectState(action, DialogManagerServiceState.RecordingIntent)) {

            timeoutJob?.cancel()
            indicationService.onSilenceDetected()
            onAction(DialogAction.StopListening(Source.Local))

        }
    }

    private suspend fun startListening(action: DialogAction.StartListening) {
        if (isInCorrectState(action, DialogManagerServiceState.Idle)) {

            sessionId?.also { id ->
                sendAudioCaptured = action.sendAudioCaptured

                wakeWordService.stopDetection()
                indicationService.onListening()
                _currentDialogState.value = DialogManagerServiceState.RecordingIntent
                CoroutineScope(Dispatchers.Default).launch {
                    speechToTextService.startSpeechToText(id)
                }
                //await silence to stop recording
                timeoutJob = coroutineScope.launch {
                    delay(recordingTimeout)
                    logger.d { "recordingTimeout" }
                    onAction(DialogAction.StopListening(Source.Local))
                }
            } ?: run {
                logger.d { "startListening parameter issue sessionId: $sessionId" }
            }

        }
    }

    /**
     * starts a session
     *
     * stops hot word service
     * shows indication that session started
     * starts recording
     * shows indication that recording started
     */
    private fun startSession(action: DialogAction.StartSession) {
        if (isInCorrectState(action, DialogManagerServiceState.Idle)) {

            wakeWordService.stopDetection()
            onAction(DialogAction.SessionStarted(Source.Local))

        }
    }

    /**
     * stop listening
     *
     * stops recording by ending speech to text
     * sends speech to mqtt if requested
     */
    private suspend fun stopListening(action: DialogAction.StopListening) {
        if (isInCorrectState(action, DialogManagerServiceState.RecordingIntent)) {

            sessionId?.also { id ->
                timeoutJob?.cancel()
                _currentDialogState.value = DialogManagerServiceState.TranscribingIntent
                indicationService.onThinking()
                speechToTextService.endSpeechToText(id, action.source is Source.Mqtt)
                if (sendAudioCaptured) {
                    mqttService.audioCaptured(id, speechToTextService.speechToTextAudioData)
                }
                //await for text recognition
                timeoutJob = coroutineScope.launch {
                    delay(textAsrTimeout)
                    logger.d { "textAsrTimeout" }
                    onAction(DialogAction.AsrError(Source.Local))
                }
            } ?: run {
                logger.d { "stopListening parameter issue sessionId: $sessionId" }
            }

        }
    }

    /**
     * checks if dialog is in the correct state and logs output
     */
    private fun isInCorrectState(action: DialogAction, vararg states: DialogManagerServiceState): Boolean {
        if (isTestMode) {
            //ignore any state in test mode
            return true
        }

        //session id irrelevant
        if(action is DialogAction.PlayAudio && states.contains(_currentDialogState.value)) {
            return true
        }

        val result = when (params.option) {
            //on local option check that state is correct and when from mqtt check session id as well
            DialogManagementOption.Local -> {
                when (action.source) {
                    Source.HttpApi,
                    Source.Local -> {
                        val result = states.contains(_currentDialogState.value)
                        if (!result) {
                            logger.d { "$action from ${action.source} called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                        }
                        return result
                    }
                    is Source.Mqtt -> {

                        //exception
                        if ((action is DialogAction.StopListening || action is DialogAction.IntentRecognitionResult) &&
                            get<SpeechToTextServiceParams>().speechToTextOption == SpeechToTextOption.RemoteMQTT) {
                            //don't ignore
                            val result = states.contains(_currentDialogState.value)
                            if (!result) {
                                logger.d { "$action from ${action.source} called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                            }
                            return result
                        }

                        //from mqtt but session id doesn't match
                        logger.d { "$action from ${action.source} but local dialog management" }
                        return false
                    }
                }
            }
            //when option is remote http depends on source
            DialogManagementOption.RemoteMQTT -> {
                when (action.source) {
                    //from webserver or local always ignore for now
                    Source.HttpApi,
                    Source.Local -> {
                        logger.d { "$action dialog management RemoteMQTT doesn't match source ${action.source}" }
                        return false
                    }
                    //from mqtt check session id
                    is Source.Mqtt -> {
                        if (sessionId == null) {
                            //update session id if none is set
                            sessionId = action.source.sessionId
                            return true
                        } else {
                            //compare session id if one is set
                            val matches = sessionId == action.source.sessionId
                            if (!matches) {
                                logger.d { "$action from mqtt but session id doesn't match $sessionId != ${action.source.sessionId}" }
                            }
                            return matches
                        }
                    }
                }
            }
            //when dialog is disabled just do and ignore state
            DialogManagementOption.Disabled -> true
        }
        if (result) {
            logger.d { action.toString() }
        }
        return result
    }


    /**
     * sends status updates to mqtt if necessary and if source is not from mqtt
     */
    private suspend fun informMqtt(action: DialogAction) {
        val safeSessionId = sessionId ?: "null"
        if (action.source !is Source.Mqtt) {
            when (action) {
                is DialogAction.AsrError -> mqttService.asrError(safeSessionId)
                is DialogAction.AsrTextCaptured -> mqttService.asrTextCaptured(safeSessionId, action.text)
                is DialogAction.WakeWordDetected -> mqttService.hotWordDetected(action.wakeWord)
                is DialogAction.IntentRecognitionError -> mqttService.intentNotRecognized(safeSessionId)
                is DialogAction.PlayFinished -> mqttService.playFinished()
                is DialogAction.SessionEnded -> mqttService.sessionEnded(safeSessionId)
                is DialogAction.SessionStarted -> mqttService.sessionStarted(safeSessionId)
                else -> {}
            }
        }
    }
}