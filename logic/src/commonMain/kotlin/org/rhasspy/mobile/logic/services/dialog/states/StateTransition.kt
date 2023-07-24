package org.rhasspy.mobile.logic.services.dialog.states

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SessionEnded
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParams
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceParamsCreator
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.RecordingIntentState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.TranscribingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.SessionData
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface IStateTransition {

    suspend fun transitionToIdleState(sessionData: SessionData?): DialogManagerState

    suspend fun transitionToRecordingState(
        sessionData: SessionData,
        isSourceMqtt: Boolean
    ): DialogManagerState

    suspend fun transitionToTranscribingIntentState(sessionData: SessionData): DialogManagerState

    suspend fun transitionToRecognizingIntentState(sessionData: SessionData): DialogManagerState

    suspend fun transitionToAudioPlayingState(audioSource: AudioSource): DialogManagerState

}

internal class StateTransition(
    paramsCreator: DialogManagerServiceParamsCreator,
    dispatcherProvider: IDispatcherProvider,
    private val dialogManagerService: IDialogManagerService,
    private val audioFocusService: IAudioFocusService,
    private val wakeWordService: IWakeWordService,
    private val intentRecognitionService: IIntentRecognitionService,
    private val audioPlayingService: IAudioPlayingService,
    private val speechToTextService: ISpeechToTextService,
    private val indicationService: IIndicationService,
    private val mqttService: IMqttService
) : IStateTransition {

    private val paramsFlow: StateFlow<DialogManagerServiceParams> = paramsCreator()
    private val params: DialogManagerServiceParams get() = paramsFlow.value

    private var coroutineScope = CoroutineScope(dispatcherProvider.IO)

    override suspend fun transitionToIdleState(sessionData: SessionData?): DialogManagerState {

        audioFocusService.abandon(AudioFocusRequestReason.Dialog)
        indicationService.onIdle()

        if (sessionData != null) {
            dialogManagerService.informMqtt(sessionData, SessionEnded(Local))
        }

        wakeWordService.startDetection()

        return DialogManagerState.IdleState(sessionData)
    }

    override suspend fun transitionToRecordingState(
        sessionData: SessionData,
        isSourceMqtt: Boolean
    ): DialogManagerState {
        wakeWordService.stopDetection()
        indicationService.onRecording()

        indicationService.onRecording()

        audioFocusService.request(AudioFocusRequestReason.Dialog)
        speechToTextService.startSpeechToText(sessionData.sessionId, isSourceMqtt)

        return RecordingIntentState(
            sessionData = sessionData,
            timeoutJob = coroutineScope.launch {
                delay(params.recordingTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(AsrError(Local))
            }
        )
    }

    override suspend fun transitionToTranscribingIntentState(sessionData: SessionData): DialogManagerState {
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
                delay(params.asrTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(AsrError(Local))
            }
        )
    }

    override suspend fun transitionToRecognizingIntentState(sessionData: SessionData): DialogManagerState {
        indicationService.onThinking()

        intentRecognitionService.recognizeIntent(
            sessionData.sessionId,
            sessionData.recognizedText ?: ""
        )

        return DialogManagerState.RecognizingIntentState(
            sessionData = sessionData,
            timeoutJob = coroutineScope.launch {
                delay(params.intentRecognitionTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(IntentRecognitionError(Local))
            }
        )
    }

    override suspend fun transitionToAudioPlayingState(audioSource: AudioSource): DialogManagerState {
        indicationService.onPlayAudio()
        audioPlayingService.stopPlayAudio()
        audioPlayingService.playAudio(audioSource)

        return DialogManagerState.AudioPlayingState()
    }

}