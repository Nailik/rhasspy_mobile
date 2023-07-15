package org.rhasspy.mobile.logic.services.dialog.states

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.RecordingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService

interface IRecordingIntentStateAction {

    suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: RecordingIntentState
    )

}

internal class RecordingIntentStateAction(
    private val dialogManagerService: IDialogManagerService,
    private val indicationService: IIndicationService,
    private val stateTransition: IStateTransition,
    private val speechToTextService: ISpeechToTextService
) : IRecordingIntentStateAction {

    override suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: RecordingIntentState,
    ) {

        when (action) {
            is AsrError        -> onAsrErrorAction(action, state)
            is AsrTextCaptured -> onAsrTextCapturedAction(action, state)
            is EndSession      -> onEndSessionAction(action, state)
            is SilenceDetected -> onSilenceDetectedAction(action, state)
            is StopListening   -> onStopListeningAction(action, state)
            else               -> Unit
        }

    }

    private suspend fun onAsrErrorAction(
        action: AsrError,
        state: RecordingIntentState
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        indicationService.onError()
        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(
                state.sessionData
            )
        )
    }

    private suspend fun onAsrTextCapturedAction(
        action: AsrTextCaptured,
        state: RecordingIntentState
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        speechToTextService.endSpeechToText(state.sessionData.sessionId, action.source is Source.Mqtt)
        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToRecognizingIntentState(
                state.sessionData.copy(recognizedText = action.text)
            )
        )
    }

    private suspend fun onEndSessionAction(
        action: EndSession,
        state: RecordingIntentState
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        speechToTextService.endSpeechToText(state.sessionData.sessionId, action.source is Source.Mqtt)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(state.sessionData)
        )
    }

    private suspend fun onSilenceDetectedAction(
        action: SilenceDetected,
        state: RecordingIntentState
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        speechToTextService.endSpeechToText(state.sessionData.sessionId, action.source is Source.Mqtt)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToTranscribingIntentState(state.sessionData)
        )
    }

    private suspend fun onStopListeningAction(
        action: StopListening,
        state: RecordingIntentState
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        speechToTextService.endSpeechToText(state.sessionData.sessionId, action.source is Source.Mqtt)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToTranscribingIntentState(state.sessionData)
        )
    }

}