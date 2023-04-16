package org.rhasspy.mobile.logic.services.dialog

import com.benasher44.uuid.uuid4
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.services.audioplaying.AudioPlayingService
import org.rhasspy.mobile.logic.services.indication.IndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordServiceParams
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.notNull
import org.rhasspy.mobile.platformspecific.readOnly
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * The Dialog Manager handles the various states and goes to the next state according to the function that is called
 */
class DialogManagerService : IService(LogType.DialogManagerService) {

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    override val serviceState = _serviceState.readOnly

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

    private val textAsrTimeout = params.asrTimeout.toDuration(DurationUnit.MILLISECONDS)
    private val intentRecognitionTimeout = params.intentRecognitionTimeout.toDuration(DurationUnit.MILLISECONDS)
    private val recordingTimeout = params.recordingTimeout.toDuration(DurationUnit.MILLISECONDS)

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
        AppSetting.isHotWordEnabled.value = true
        wakeWordService.startDetection()
    }

    fun onAction(action: DialogServiceMiddlewareAction) {
        logger.d { "onAction $action" }
        coroutineScope.launch {
            when (action) {
                is DialogServiceMiddlewareAction.AsrError -> asrError(action)
                is DialogServiceMiddlewareAction.AsrTextCaptured -> asrTextCaptured(action)
                is DialogServiceMiddlewareAction.EndSession -> endSession(action)
                is DialogServiceMiddlewareAction.WakeWordDetected -> wakeWordDetected(action)
                is DialogServiceMiddlewareAction.IntentRecognitionResult -> intentRecognitionResult(action)
                is DialogServiceMiddlewareAction.IntentRecognitionError -> intentRecognitionError(action)
                is DialogServiceMiddlewareAction.PlayAudio -> playAudio(action)
                is DialogServiceMiddlewareAction.StopAudioPlaying -> stopPlayAudio(action)
                is DialogServiceMiddlewareAction.PlayFinished -> playFinished(action)
                is DialogServiceMiddlewareAction.SessionEnded -> sessionEnded(action)
                is DialogServiceMiddlewareAction.SessionStarted -> sessionStarted(action)
                is DialogServiceMiddlewareAction.SilenceDetected -> silenceDetected(action)
                is DialogServiceMiddlewareAction.StartListening -> startListening(action)
                is DialogServiceMiddlewareAction.StartSession -> startSession(action)
                is DialogServiceMiddlewareAction.StopListening -> stopListening(action)
            }
        }
    }

    /**
     * Asr Error occurs, when the speech could not be translated to text, this will result in a failed dialog
     *
     * plays error sound
     * ends session
     */
    private suspend fun asrError(action: DialogServiceMiddlewareAction.AsrError) {
        if (isInCorrectState(action, DialogManagerServiceState.TranscribingIntent, DialogManagerServiceState.RecordingIntent)) {

            if (_currentDialogState.value == DialogManagerServiceState.RecordingIntent) {
                speechToTextService.endSpeechToText(sessionId ?: "", action.source is Source.Mqtt)
            }

            timeoutJob?.cancel()
            indicationService.onError()
            informMqtt(action)
            onAction(DialogServiceMiddlewareAction.SessionEnded(Source.Local))

        }
    }


    /**
     * the speech could be translated to text
     *
     * next step is to translate the text to an intent
     */
    private suspend fun asrTextCaptured(action: DialogServiceMiddlewareAction.AsrTextCaptured) {
        if (isInCorrectState(action, DialogManagerServiceState.TranscribingIntent, DialogManagerServiceState.RecordingIntent)) {

            if (_currentDialogState.value == DialogManagerServiceState.RecordingIntent) {
                speechToTextService.endSpeechToText(sessionId ?: "", action.source is Source.Mqtt)
            }

            timeoutJob?.cancel()
            _currentDialogState.value = DialogManagerServiceState.RecognizingIntent
            indicationService.onThinking()
            informMqtt(action)

            notNull(sessionId, action.text, { id, text ->

                if (params.option == DialogManagementOption.Local) {
                    //only do action on local dialog management
                    intentRecognitionService.recognizeIntent(id, text)
                    //await intent recognition
                    timeoutJob = coroutineScope.launch {
                        delay(intentRecognitionTimeout)
                        if (_currentDialogState.value == DialogManagerServiceState.RecognizingIntent) {
                            logger.d { "intentRecognitionTimeout" }
                            onAction(DialogServiceMiddlewareAction.IntentRecognitionError(Source.Local))
                        }
                    }
                }
            }, {
                logger.d { "asrTextCaptured parameter issue sessionId: $sessionId action.text: ${action.text}" }
                onAction(DialogServiceMiddlewareAction.IntentRecognitionError(Source.Local))
            })

        }
    }

    /**
     * end the session nominally after handling intent
     *
     * next step is to invoke ended session which will start a new one
     */
    private suspend fun endSession(action: DialogServiceMiddlewareAction.EndSession) {
        if (isInCorrectState(
                action,
                DialogManagerServiceState.RecordingIntent,
                DialogManagerServiceState.HandlingIntent,
                DialogManagerServiceState.TranscribingIntent,
                DialogManagerServiceState.RecognizingIntent
            )
        ) {

            if (_currentDialogState.value == DialogManagerServiceState.RecordingIntent) {
                speechToTextService.endSpeechToText(sessionId ?: "", action.source is Source.Mqtt)
            }

            onAction(DialogServiceMiddlewareAction.SessionEnded(Source.Local))

        }
    }

    /**
     * called when a hotWord was detected either by mqtt, local or via http api
     *
     * starts a session
     */
    private suspend fun wakeWordDetected(action: DialogServiceMiddlewareAction.WakeWordDetected) {
        if (isInCorrectState(action, DialogManagerServiceState.AwaitingWakeWord)) {

            indicationService.onWakeWordDetected {

                _currentDialogState.value = DialogManagerServiceState.Idle
                coroutineScope.launch {
                    informMqtt(action)
                }
                onAction(DialogServiceMiddlewareAction.StartSession(Source.Local))
            }

        }
    }

    /**
     * intent was recognized from text
     *
     * next step is to handle this intent
     */
    private suspend fun intentRecognitionResult(action: DialogServiceMiddlewareAction.IntentRecognitionResult) {
        if (isInCorrectState(action, DialogManagerServiceState.RecognizingIntent)) {

            timeoutJob?.cancel()
            intentHandlingService.intentHandling(action.intentName, action.intent)
            onAction(DialogServiceMiddlewareAction.EndSession(Source.Local))

        }
    }

    private suspend fun intentRecognitionError(action: DialogServiceMiddlewareAction.IntentRecognitionError) {
        if (isInCorrectState(action, DialogManagerServiceState.RecognizingIntent)) {

            timeoutJob?.cancel()
            informMqtt(action)
            indicationService.onError()
            onAction(DialogServiceMiddlewareAction.SessionEnded(Source.Local))

        }
    }

    /**
     * play audio was invoked, probably playing speech
     *
     * stop the hot word detection
     * play the audio
     */
    private suspend fun playAudio(action: DialogServiceMiddlewareAction.PlayAudio) {

        indicationService.onPlayAudio()
        audioPlayingService.stopPlayAudio()
        @Suppress("DEPRECATION")
        audioPlayingService.playAudio(AudioSource.Data(action.byteArray))

    }

    /**
     * stops to play the current audio
     */
    @Suppress("UNUSED_PARAMETER")
    private fun stopPlayAudio(action: DialogServiceMiddlewareAction.StopAudioPlaying) {

        audioPlayingService.stopPlayAudio()
        onAction(DialogServiceMiddlewareAction.PlayFinished(Source.Local))

    }

    /**
     * playing audio finished
     *
     * go back to awaiting hotword
     * start hot word service
     * tell mqtt (if source is not mqtt)
     */
    private suspend fun playFinished(action: DialogServiceMiddlewareAction.PlayFinished) {

        when (_currentDialogState.value) {
            DialogManagerServiceState.Idle,
            DialogManagerServiceState.AwaitingWakeWord -> indicationService.onIdle()

            DialogManagerServiceState.RecordingIntent -> indicationService.onListening()
            DialogManagerServiceState.TranscribingIntent,
            DialogManagerServiceState.RecognizingIntent,
            DialogManagerServiceState.HandlingIntent -> indicationService.onThinking()
        }
        informMqtt(action)

    }

    /**
     * when a session has ended
     *
     * start hotWord service again
     * await hotWord
     * tell mqtt
     */
    private suspend fun sessionEnded(action: DialogServiceMiddlewareAction.SessionEnded) {
        if (isInCorrectState(
                action,
                DialogManagerServiceState.TranscribingIntent,
                DialogManagerServiceState.HandlingIntent,
                DialogManagerServiceState.RecognizingIntent
            )
        ) {

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
    private suspend fun sessionStarted(action: DialogServiceMiddlewareAction.SessionStarted) {
        if (isInCorrectState(action, DialogManagerServiceState.Idle)) {

            val newSessionId = when (action.source) {
                Source.HttpApi -> null
                Source.Local -> uuid4().toString()
                is Source.Mqtt -> action.source.sessionId
            }

            if (newSessionId != null) {
                sessionId = newSessionId
                informMqtt(action)
                onAction(DialogServiceMiddlewareAction.StartListening(Source.Local, false))
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
    private fun silenceDetected(action: DialogServiceMiddlewareAction.SilenceDetected) {
        if (isInCorrectState(action, DialogManagerServiceState.RecordingIntent)) {

            timeoutJob?.cancel()
            indicationService.onSilenceDetected()
            onAction(DialogServiceMiddlewareAction.StopListening(Source.Local))

        }
    }

    private suspend fun startListening(action: DialogServiceMiddlewareAction.StartListening) {
        if (isInCorrectState(action, DialogManagerServiceState.Idle)) {

            sessionId?.also { id ->
                sendAudioCaptured = action.sendAudioCaptured

                wakeWordService.stopDetection()
                indicationService.onListening()
                _currentDialogState.value = DialogManagerServiceState.RecordingIntent
                CoroutineScope(Dispatchers.Default).launch {
                    speechToTextService.startSpeechToText(id, action.source is Source.Mqtt)
                }

                if (params.option == DialogManagementOption.Local) {
                    //await silence to stop recording
                    timeoutJob = coroutineScope.launch {
                        delay(recordingTimeout)
                        logger.d { "recordingTimeout" }
                        onAction(DialogServiceMiddlewareAction.StopListening(Source.Local))
                    }
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
    private fun startSession(action: DialogServiceMiddlewareAction.StartSession) {
        if (isInCorrectState(action, DialogManagerServiceState.Idle)) {

            wakeWordService.stopDetection()
            onAction(DialogServiceMiddlewareAction.SessionStarted(Source.Local))

        }
    }

    /**
     * stop listening
     *
     * stops recording by ending speech to text
     * sends speech to mqtt if requested
     */
    private suspend fun stopListening(action: DialogServiceMiddlewareAction.StopListening) {
        if (isInCorrectState(action, DialogManagerServiceState.RecordingIntent)) {

            sessionId?.also { id ->
                timeoutJob?.cancel()
                _currentDialogState.value = DialogManagerServiceState.TranscribingIntent
                indicationService.onThinking()
                speechToTextService.endSpeechToText(id, action.source is Source.Mqtt)
                if (sendAudioCaptured) {
                    mqttService.audioCaptured(id, speechToTextService.speechToTextAudioFile)
                }

                if (params.option == DialogManagementOption.Local) {
                    //await for text recognition
                    timeoutJob = coroutineScope.launch {
                        delay(textAsrTimeout)
                        logger.d { "textAsrTimeout" }
                        onAction(DialogServiceMiddlewareAction.AsrError(Source.Local))
                    }
                }
            } ?: run {
                logger.d { "stopListening parameter issue sessionId: $sessionId" }
            }

        }
    }

    /**
     * checks if dialog is in the correct state and logs output
     */
    private fun isInCorrectState(
        action: DialogServiceMiddlewareAction,
        vararg states: DialogManagerServiceState
    ): Boolean {
        //session id irrelevant
        if (action is DialogServiceMiddlewareAction.PlayAudio && states.contains(_currentDialogState.value)) {
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
                            logger.d { "$action called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                        }
                        return result
                    }

                    is Source.Mqtt -> {

                        //exceptions where calls form mqtt are ok
                        val doNotIgnore = when (action) {
                            is DialogServiceMiddlewareAction.WakeWordDetected -> {
                                val wakeWordOption = get<WakeWordServiceParams>().wakeWordOption
                                return wakeWordOption == WakeWordOption.MQTT || wakeWordOption == WakeWordOption.Udp
                            }

                            is DialogServiceMiddlewareAction.AsrError,
                            is DialogServiceMiddlewareAction.AsrTextCaptured -> get<SpeechToTextServiceParams>().speechToTextOption == SpeechToTextOption.RemoteMQTT

                            is DialogServiceMiddlewareAction.IntentRecognitionError,
                            is DialogServiceMiddlewareAction.IntentRecognitionResult -> get<IntentRecognitionServiceParams>().intentRecognitionOption == IntentRecognitionOption.RemoteMQTT

                            else -> false
                        }

                        //exception
                        if (doNotIgnore) {
                            //don't ignore
                            val result = states.contains(_currentDialogState.value)
                            if (!result) {
                                logger.d { "$action called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                            }
                            return result
                        } else {
                            //from mqtt but session id doesn't match
                            logger.d { "$action but local dialog management" }
                            return false
                        }
                    }
                }
            }
            //when option is remote http depends on source
            DialogManagementOption.RemoteMQTT -> {
                when (action.source) {
                    //from webserver or local always ignore for now
                    Source.HttpApi,
                    Source.Local -> {
                        val doNotIgnore = when (action) {
                            is DialogServiceMiddlewareAction.WakeWordDetected -> true
                            is DialogServiceMiddlewareAction.PlayFinished -> true
                            is DialogServiceMiddlewareAction.StopListening -> true //maybe called by user clicking button
                            else -> false
                        }

                        if (doNotIgnore) {
                            //don't ignore
                            val result = states.contains(_currentDialogState.value)
                            if (!result) {
                                logger.d { "$action called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                            }
                            return result
                        } else {
                            //from mqtt but session id doesn't match
                            logger.d { "$action but mqtt dialog management" }
                            return false
                        }
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
                                logger.d { "$action but session id doesn't match $sessionId != ${action.source.sessionId}" }
                                return false
                            }
                            val result = states.contains(_currentDialogState.value)
                            if (!result) {
                                logger.d { "$action called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                            }
                            return result
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
    private suspend fun informMqtt(action: DialogServiceMiddlewareAction) {
        val safeSessionId = sessionId ?: "null"
        if (action.source !is Source.Mqtt) {
            when (action) {
                is DialogServiceMiddlewareAction.AsrError -> mqttService.asrError(safeSessionId)
                is DialogServiceMiddlewareAction.AsrTextCaptured -> mqttService.asrTextCaptured(safeSessionId, action.text)
                is DialogServiceMiddlewareAction.WakeWordDetected -> mqttService.hotWordDetected(action.wakeWord)
                is DialogServiceMiddlewareAction.IntentRecognitionError -> mqttService.intentNotRecognized(safeSessionId)
                is DialogServiceMiddlewareAction.PlayFinished -> mqttService.playFinished()
                is DialogServiceMiddlewareAction.SessionEnded -> mqttService.sessionEnded(safeSessionId)
                is DialogServiceMiddlewareAction.SessionStarted -> mqttService.sessionStarted(safeSessionId)
                else -> {}
            }
        }
    }
}