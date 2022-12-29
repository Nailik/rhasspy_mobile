package org.rhasspy.mobile.services.dialogManager

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.Source
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.hotword.HotWordService
import org.rhasspy.mobile.services.indication.IndicationService
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService

/**
 * The Dialog Manager handles the various states and goes to the next state according to the function that is called
 */
class DialogManagerService : IService() {

    private val logger = Logger.withTag("DialogManagerService")

    private val params by inject<DialogManagerServiceParams>()
    private val hotWordService by inject<HotWordService>()
    private val rhasspyActionsService by inject<RhasspyActionsService>()
    private val indicationService by inject<IndicationService>()
    private val mqttService by inject<MqttService>()
    private var sessionId: String? = null
    private var coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _currentDialogState = MutableStateFlow(DialogManagerServiceState.Idle)
    val currentDialogState = _currentDialogState.readOnly

    //TODO timeout awaiting next state
    override fun onClose() {
        coroutineScope.cancel()
    }


    fun onAction(action: DialogAction) {
        coroutineScope.launch {
            when (action) {
                is DialogAction.AsrError -> asrError(action)
                is DialogAction.AsrTextCaptured -> asrTextCaptured(action)
                is DialogAction.EndSession -> endSession(action)
                is DialogAction.HotWordDetected -> hotWordDetected(action)
                is DialogAction.IntentRecognitionResult -> intentRecognitionResult(action)
                is DialogAction.IntentRecognitionError -> intentRecognitionError(action)
                is DialogAction.PlayAudio -> playAudio(action)
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
    //TODO do things when rhasspy action finished, had error etc

    /**
     * Asr Error occurs, when the speech could not be translated to text, this will result in a failed dialog
     *
     * plays error sound
     * ends session
     */
    private suspend fun asrError(action: DialogAction.AsrError) {
        if (isInCorrectState(action, DialogManagerServiceState.TranscribingIntent)) {

            indicationService.onError()
            informMqtt(action)
            sessionEnded(DialogAction.SessionEnded(Source.Local))

        }
    }


    /**
     * the speech could be translated to text
     *
     * next step is to translate the text to an intent
     */
    private suspend fun asrTextCaptured(action: DialogAction.AsrTextCaptured) {
        if (isInCorrectState(action, DialogManagerServiceState.TranscribingIntent)) {

            _currentDialogState.value = DialogManagerServiceState.RecognizingIntent
            informMqtt(action)
            rhasspyActionsService.recognizeIntent(sessionId ?: "", action.text ?: "")

        }
    }

    /**
     * end the session nominally after handling intent
     *
     * next step is to invoke ended session which will start a new one
     */
    private suspend fun endSession(action: DialogAction.EndSession) {
        if (isInCorrectState(action, DialogManagerServiceState.HandlingIntent)) {

            _currentDialogState.value = DialogManagerServiceState.Idle
            sessionEnded(DialogAction.SessionEnded(Source.Local))

        }
    }

    /**
     * called when a hotWord was detected either by mqtt, local or via http api
     *
     * starts a session
     */
    private suspend fun hotWordDetected(action: DialogAction.HotWordDetected) {
        if (isInCorrectState(action, DialogManagerServiceState.AwaitingHotWord)) {

            _currentDialogState.value = DialogManagerServiceState.Idle
            informMqtt(action)
            startSession(DialogAction.StartSession(Source.Local))

        }
    }

    /**
     * intent was recognized from text
     *
     * next step is to handle this intent
     */
    private suspend fun intentRecognitionResult(action: DialogAction.IntentRecognitionResult) {
        if (isInCorrectState(action, DialogManagerServiceState.RecognizingIntent)) {

            rhasspyActionsService.intentHandling(action.intentName ?: "", action.intent)
            endSession(DialogAction.EndSession(Source.Local))

        }
    }

    private suspend fun intentRecognitionError(action: DialogAction.IntentRecognitionError) {
        if (isInCorrectState(action, DialogManagerServiceState.RecognizingIntent)) {

            informMqtt(action)
            indicationService.onError()
            sessionEnded(DialogAction.SessionEnded(Source.Local))

        }
    }

    /**
     * play audio was invoked, probably playing speech
     *
     * stop the hot word detection
     * play the audio
     */
    private suspend fun playAudio(action: DialogAction.PlayAudio) {
        if (isInCorrectState(action, DialogManagerServiceState.HandlingIntent, DialogManagerServiceState.Idle, DialogManagerServiceState.AwaitingHotWord)) {

            hotWordService.stopDetection()
            rhasspyActionsService.playAudio(action.byteArray.toList())

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

            _currentDialogState.value = DialogManagerServiceState.AwaitingHotWord
            informMqtt(action)
            hotWordService.stopDetection()

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
        if (isInCorrectState(action, DialogManagerServiceState.TranscribingIntent, DialogManagerServiceState.HandlingIntent)) {

            sessionId = null
            _currentDialogState.value = DialogManagerServiceState.AwaitingHotWord
            informMqtt(action)
            hotWordService.startDetection()

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

            sessionId = when (action.source) {
                Source.HttpApi -> "" //TODO error??
                Source.Local -> uuid4().toString()
                is Source.Mqtt -> action.source.sessionId ?: "" //TODO error
            }
            informMqtt(action)
            onAction(DialogAction.StartListening(Source.Local, false))

        }
    }

    /**
     * when silence is detected
     *
     * stop listening action
     */
    private fun silenceDetected(action: DialogAction.SilenceDetected) {
        if (isInCorrectState(action, DialogManagerServiceState.RecordingIntent)) {

            onAction(DialogAction.StopListening(Source.Local))

        }
    }

    private suspend fun startListening(action: DialogAction.StartListening) {
        if (isInCorrectState(action, DialogManagerServiceState.Idle)) {

            hotWordService.stopDetection()
            indicationService.onRecording()
            rhasspyActionsService.startSpeechToText(sessionId ?: "") //TODO error

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

            hotWordService.stopDetection()
            indicationService.onWakeUp()
            onAction(DialogAction.SessionStarted(Source.Local))

        }
    }

    /**
     * stop listening
     *
     * stops recording by ending speech to text
     */
    private suspend fun stopListening(action: DialogAction.StopListening) {
        if (isInCorrectState(action, DialogManagerServiceState.RecordingIntent)) {

            rhasspyActionsService.endSpeechToText(sessionId ?: "", action.source is Source.Mqtt)

        }
    }

    /**
     * checks if dialog is in the correct state and logs output
     */
    private fun isInCorrectState(action: DialogAction, vararg states: DialogManagerServiceState): Boolean {
        return when (params.option) {
            //on local option check that state is correct and when from mqtt check session id as well
            DialogManagementOptions.Local -> {
                if (action.source is Source.Mqtt && sessionId != action.source.sessionId) {
                    //from mqtt but session id doesn't match
                    return false
                }

                val result = states.contains(_currentDialogState.value)
                if (result) {
                    logger.d { action.toString() }
                } else {
                    logger.w { "$action called in wrong state ${_currentDialogState.value} expected one of ${states.joinToString()}" }
                }
                return result
            }
            //when option is remote http depends on source
            DialogManagementOptions.RemoteMQTT -> {
                when (action.source) {
                    //from webserver or local always ignore for now
                    Source.HttpApi,
                    Source.Local -> false
                    //from mqtt check session id
                    is Source.Mqtt -> {
                        if (sessionId == null) {
                            //update session id if none is set
                            sessionId = action.source.sessionId
                            return true
                        } else {
                            //compare session id if one is set
                            return sessionId == action.source.sessionId
                        }
                    }
                }
            }
            //when dialog is disabled just do and ignore state
            DialogManagementOptions.Disabled -> true
        }
    }


    /**
     * sends status updates to mqtt if necessary and if source is not from mqtt
     */
    private suspend fun informMqtt(action: DialogAction){
        val safeSessionId = sessionId ?: "null"
        if(action.source !is Source.Mqtt){
            when(action) {
                is DialogAction.AsrError -> mqttService.asrError(safeSessionId)
                is DialogAction.AsrTextCaptured -> mqttService.asrTextCaptured(safeSessionId, action.text)
                is DialogAction.HotWordDetected -> mqttService.hotWordDetected(action.hotWord)
                is DialogAction.IntentRecognitionError -> mqttService.intentNotRecognized(safeSessionId)
                is DialogAction.PlayFinished -> mqttService.playFinished()
                is DialogAction.SessionEnded -> mqttService.sessionEnded(safeSessionId)
                is DialogAction.SessionStarted -> mqttService.sessionStarted(safeSessionId)
                else -> {}
            }
        }
    }
}