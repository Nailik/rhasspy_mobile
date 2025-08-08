package org.rhasspy.mobile.logic.services.dialog.states

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTimeoutError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionTimeoutError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SessionEnded
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParamsCreator
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.IdleState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.PlayingAudioState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.SessionState.RecognizingIntentState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.SessionState.RecordingIntentState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.SessionState.TranscribingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.SessionData
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface IStateTransition {

    fun transitionToIdleState(sessionData: SessionData?, isSourceMqtt: Boolean): DialogManagerState

    fun transitionToRecordingState(
        sessionData: SessionData,
        isSourceMqtt: Boolean
    ): DialogManagerState

    fun transitionToTranscribingIntentState(sessionData: SessionData): DialogManagerState

    fun transitionToRecognizingIntentState(sessionData: SessionData): DialogManagerState

    fun transitionToAudioPlayingState(): DialogManagerState

}

internal class StateTransition(
    paramsCreator: DialogManagerServiceParamsCreator,
    dispatcherProvider: IDispatcherProvider,
    private val dialogManagerService: IDialogManagerService,
    private val audioFocusService: IAudioFocusService,
    private val wakeWordService: IWakeWordService,
    private val intentRecognitionService: IIntentRecognitionService,
    private val speechToTextService: ISpeechToTextService,
    private val indicationService: IIndicationService,
    private val mqttService: IMqttService
) : IStateTransition {

    private val paramsFlow: StateFlow<DialogManagerServiceParams> = paramsCreator()
    private val params: DialogManagerServiceParams get() = paramsFlow.value

    private var coroutineScope = CoroutineScope(dispatcherProvider.IO)

    override fun transitionToIdleState(
        sessionData: SessionData?,
        isSourceMqtt: Boolean
    ): DialogManagerState {

        audioFocusService.abandon(AudioFocusRequestReason.Dialog)
        indicationService.onIdle()

        if (!isSourceMqtt && sessionData != null) {
            dialogManagerService.informMqtt(sessionData, SessionEnded(Local))
        }

        wakeWordService.startDetection()

        return IdleState
    }

    override fun transitionToRecordingState(
        sessionData: SessionData,
        isSourceMqtt: Boolean
    ): DialogManagerState {

        wakeWordService.stopDetection()
        indicationService.onRecording()

        audioFocusService.request(AudioFocusRequestReason.Dialog)
        speechToTextService.startSpeechToText(sessionData.sessionId, isSourceMqtt)

        return RecordingIntentState(
            sessionData = sessionData,
            timeoutJob = coroutineScope.launch {
                if (params.recordingTimeout == 0L) return@launch
                delay(params.recordingTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(AsrTimeoutError(Local))
            }
        )
    }

    override fun transitionToTranscribingIntentState(sessionData: SessionData): DialogManagerState {

        indicationService.onThinking()

        if (sessionData.sendAudioCaptured) {
            mqttService.audioCaptured(
                sessionData.sessionId,
                speechToTextService.speechToTextAudioFile
            )
        }

        return TranscribingIntentState(
            sessionData = sessionData,
            timeoutJob = coroutineScope.launch {
                if (params.asrTimeout == 0L) return@launch
                delay(params.asrTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(AsrTimeoutError(Local))
            }
        )
    }

    override fun transitionToRecognizingIntentState(sessionData: SessionData): DialogManagerState {

        indicationService.onThinking()

        intentRecognitionService.recognizeIntent(
            sessionId = sessionData.sessionId,
            text = sessionData.recognizedText ?: ""
        )

        return RecognizingIntentState(
            sessionData = sessionData,
            timeoutJob = coroutineScope.launch {
                if (params.intentRecognitionTimeout == 0L) return@launch
                delay(params.intentRecognitionTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(IntentRecognitionTimeoutError(Local))
            }
        )
    }

    override fun transitionToAudioPlayingState(): DialogManagerState {
        return PlayingAudioState
    }

}