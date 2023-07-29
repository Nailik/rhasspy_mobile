package org.rhasspy.mobile.logic.services.dialog.states

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.audiofocus.AudioFocusRequestReason
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.logic.services.audiofocus.IAudioFocusService
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.*
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.RecordingIntentState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.TranscribingIntentState
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.settings.AppSetting
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface IStateTransition {

    fun transitionToIdleState(sessionData: SessionData?): DialogManagerState

    fun transitionToRecordingState(sessionData: SessionData, isSourceMqtt: Boolean): DialogManagerState

    fun transitionToTranscribingIntentState(sessionData: SessionData): DialogManagerState

    fun transitionToRecognizingIntentState(sessionData: SessionData): DialogManagerState

    fun transitionToAudioPlayingState(audioSource: AudioSource): DialogManagerState

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

    override fun transitionToIdleState(sessionData: SessionData?): DialogManagerState {

        audioFocusService.abandon(AudioFocusRequestReason.Dialog)
        indicationService.onIdle()

        if (sessionData != null) {
            dialogManagerService.informMqtt(sessionData, SessionEnded(Local))
        }

        AppSetting.isHotWordEnabled.value = true
        wakeWordService.startDetection()

        return DialogManagerState.IdleState(sessionData)
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
                delay(params.recordingTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(AsrError(Local))
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
                delay(params.asrTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(AsrError(Local))
            }
        )
    }

    override fun transitionToRecognizingIntentState(sessionData: SessionData): DialogManagerState {
        indicationService.onThinking()

        intentRecognitionService.recognizeIntent(
            sessionId = sessionData.sessionId,
            text = sessionData.recognizedText ?: ""
        )

        return DialogManagerState.RecognizingIntentState(
            sessionData = sessionData,
            timeoutJob = coroutineScope.launch {
                delay(params.intentRecognitionTimeout.toDuration(DurationUnit.MILLISECONDS))
                dialogManagerService.onAction(IntentRecognitionError(Local))
            }
        )
    }

    override fun transitionToAudioPlayingState(audioSource: AudioSource): DialogManagerState {
        indicationService.onPlayAudio()
        audioPlayingService.stopPlayAudio()
        audioPlayingService.playAudio(audioSource)

        return DialogManagerState.PlayingAudioState()
    }

}