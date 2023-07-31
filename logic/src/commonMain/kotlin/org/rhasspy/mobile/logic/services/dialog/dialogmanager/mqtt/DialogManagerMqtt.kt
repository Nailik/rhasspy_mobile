package org.rhasspy.mobile.logic.services.dialog.dialogmanager.mqtt

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.middleware.Source.Mqtt
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.SessionState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.SessionState.RecordingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.SessionData
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.IDialogManager
import org.rhasspy.mobile.logic.services.dialog.states.IStateTransition
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource

class DialogManagerMqtt(
    private val dialogManagerService: IDialogManagerService,
    private val indicationService: IIndicationService,
    private val stateTransition: IStateTransition,
    private val speechToTextService: ISpeechToTextService,
    private val intentHandlingService: IIntentHandlingService,
    private val audioPlayingService: IAudioPlayingService,
) : IDialogManager {

    private val logger = Logger.withTag("DialogManagerMqtt")

    override fun onAction(action: DialogServiceMiddlewareAction) {
        with(dialogManagerService.currentDialogState.value) {
            logger.d { "action $action on state $this" }
            when (action) {
                is AsrError                      -> dialogManagerService.addToHistory(action)
                is AsrTextCaptured               -> dialogManagerService.addToHistory(action)
                is EndSession                    -> dialogManagerService.addToHistory(action)
                is IntentRecognitionError        -> dialogManagerService.addToHistory(action)
                is IntentRecognitionResult       -> onIntentRecognitionResult(action, this)
                is PlayAudio                     -> onPlayAudio(action)
                is PlayFinished                  -> onPlayFinished(action)
                is SessionEnded                  -> onSessionEnded(action, this)
                is SessionStarted                -> dialogManagerService.addToHistory(action)
                is SilenceDetected               -> onSilenceDetected(action, this)
                is StartListening                -> onStartListening(action)
                is StartSession                  -> dialogManagerService.addToHistory(action)
                is StopAudioPlaying              -> onStopAudioPlaying(action)
                is StopListening                 -> onStopListening(action, this)
                is WakeWordDetected              -> onWakeWordDetected(action)
                is AsrTimeoutError               -> onAsrTimeoutError(action, this)
                is IntentRecognitionTimeoutError -> onIntentRecognitionTimeoutError(action, this)
            }
        }
    }

    private fun onIntentRecognitionResult(action: IntentRecognitionResult, state: DialogManagerState) {
        state.stopTimeoutJob()

        stopSpeechToTextService(action, state)

        dialogManagerService.informMqtt(state.sessionDataOrNull(), action)

        intentHandlingService.intentHandling(action.intentName, action.intent)

        dialogManagerService.addToHistory(action)
    }

    private fun onPlayAudio(action: PlayAudio) {
        dialogManagerService.informMqtt(null, action)

        indicationService.onPlayAudio()
        audioPlayingService.stopPlayAudio()

        @Suppress("DEPRECATION")
        audioPlayingService.playAudio(AudioSource.Data(action.byteArray))
        dialogManagerService.addToHistory(action)
    }

    private fun onPlayFinished(action: PlayFinished) {
        if (action.source == Source.Local) {
            dialogManagerService.informMqtt(null, action)
        }
        dialogManagerService.addToHistory(action)
    }

    private fun onSessionEnded(action: SessionEnded, state: DialogManagerState) {
        state.stopTimeoutJob()

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionDataOrNull(),
                isSourceMqtt = action.source is Mqtt
            )
        )
    }

    private fun onSilenceDetected(action: SilenceDetected, state: DialogManagerState) {
        state.stopTimeoutJob()

        indicationService.onSilenceDetected()
        dialogManagerService.informMqtt(state.sessionDataOrNull(), action)

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToTranscribingIntentState(state.sessionDataOrDummy())
        )
    }

    private fun onStartListening(action: StartListening) {
        val sessionData = SessionData(
            sessionId = getNewSessionId(action.source),
            sendAudioCaptured = action.sendAudioCaptured,
            wakeWord = null,
            recognizedText = null
        )

        indicationService.onSessionStarted()

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToRecordingState(
                sessionData = sessionData,
                isSourceMqtt = action.source is Mqtt
            )
        )
    }

    private fun onStopAudioPlaying(action: StopAudioPlaying) {
        audioPlayingService.stopPlayAudio()

        dialogManagerService.informMqtt(null, PlayFinished(action.source))

        dialogManagerService.addToHistory(action)
    }

    private fun onStopListening(action: StopListening, state: DialogManagerState) {
        state.stopTimeoutJob()

        indicationService.onSilenceDetected()
        dialogManagerService.informMqtt(state.sessionDataOrNull(), action)

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        if (state is RecordingIntentState) {
            dialogManagerService.transitionTo(
                state = stateTransition.transitionToTranscribingIntentState(state.sessionData)
            )
        }
    }

    private fun onWakeWordDetected(action: WakeWordDetected) {
        val sessionData = SessionData(
            sessionId = getNewSessionId(action.source),
            sendAudioCaptured = false,
            wakeWord = action.wakeWord,
            recognizedText = null
        )

        dialogManagerService.informMqtt(sessionData, action)
    }

    private fun onAsrTimeoutError(action: AsrTimeoutError, state: DialogManagerState) {
        state.stopTimeoutJob()

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionDataOrNull(),
                isSourceMqtt = action.source is Mqtt
            )
        )
    }

    private fun onIntentRecognitionTimeoutError(action: IntentRecognitionTimeoutError, state: DialogManagerState) {
        state.stopTimeoutJob()

        stopSpeechToTextService(action, state)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = state.sessionDataOrNull(),
                isSourceMqtt = action.source is Mqtt
            )
        )
    }

    private fun stopSpeechToTextService(action: DialogServiceMiddlewareAction, state: DialogManagerState) {
        state.stopTimeoutJob()

        if (speechToTextService.isActive) {
            speechToTextService.endSpeechToText(
                sessionId = state.sessionIdOrEmpty(),
                fromMqtt = action.source is Mqtt
            )
        }
    }

    private fun DialogManagerState.sessionIdOrEmpty(): String {
        return if (this is SessionState) this.sessionData.sessionId else ""
    }

    private fun DialogManagerState.sessionDataOrNull(): SessionData? {
        return if (this is SessionState) this.sessionData else null
    }

    private fun DialogManagerState.sessionDataOrDummy(): SessionData {
        return if (this is SessionState) this.sessionData else SessionData(
            sessionId = "Dummy",
            sendAudioCaptured = false,
            wakeWord = "",
            recognizedText = null
        )
    }

    private fun DialogManagerState.stopTimeoutJob() {
        if (this is SessionState) this.timeoutJob.cancel()
    }

    private fun getNewSessionId(source: Source): String {
        return when (source) {
            Source.HttpApi -> uuid4().toString()
            Source.Local   -> uuid4().toString()
            is Mqtt        -> source.sessionId ?: uuid4().toString()
        }
    }

}