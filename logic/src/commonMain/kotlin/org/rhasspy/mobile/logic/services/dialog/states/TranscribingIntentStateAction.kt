package org.rhasspy.mobile.logic.services.dialog.states

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.TranscribingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.indication.IIndicationService

interface ITranscribingIntentStateAction {

    suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: TranscribingIntentState
    )

}

internal class TranscribingIntentStateAction(
    private val dialogManagerService: IDialogManagerService,
    private val indicationService: IIndicationService,
    private val stateTransition: IStateTransition
) : ITranscribingIntentStateAction {

    override suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: TranscribingIntentState
    ) {

        when (action) {
            is AsrError        -> onAsrErrorAction(state, action)
            is AsrTextCaptured -> onAsrTextCapturedAction(state, action)
            is EndSession      -> onEndSessionAction(state, action)
            else               -> Unit
        }

    }

    private suspend fun onAsrErrorAction(
        state: TranscribingIntentState,
        action: AsrError
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        indicationService.onError()

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(state.sessionData)
        )
    }

    private suspend fun onAsrTextCapturedAction(
        state: TranscribingIntentState,
        action: AsrTextCaptured
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToRecognizingIntentState(
                state.sessionData.copy(recognizedText = action.text)
            )
        )
    }

    private suspend fun onEndSessionAction(
        state: TranscribingIntentState,
        action: EndSession
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(state.sessionData)
        )
    }

}