package org.rhasspy.mobile.logic.services.dialog.states

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.RecognizingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IIntentHandlingService

interface IRecognizingIntentStateAction {

    suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: RecognizingIntentState
    )

}

internal class RecognizingIntentStateAction(
    private val dialogManagerService: IDialogManagerService,
    private val indicationService: IIndicationService,
    private val stateTransition: IStateTransition,
    private val intentHandlingService: IIntentHandlingService,
) : IRecognizingIntentStateAction {

    override suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: RecognizingIntentState
    ) {

        when (action) {
            is EndSession              -> onEndSessionAction(action, state)
            is IntentRecognitionError  -> onIntentRecognitionErrorAction(action, state)
            is IntentRecognitionResult -> onIntentRecognitionResultAction(action, state)
            else                       -> Unit
        }

    }

    private suspend fun onEndSessionAction(
        action: EndSession,
        state: RecognizingIntentState
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(state.sessionData)
        )
    }

    private suspend fun onIntentRecognitionErrorAction(
        action: IntentRecognitionError,
        state: RecognizingIntentState
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        indicationService.onError()
        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(state.sessionData)
        )
    }

    private suspend fun onIntentRecognitionResultAction(
        action: IntentRecognitionResult,
        state: RecognizingIntentState
    ) {
        state.timeoutJob.cancel()
        dialogManagerService.informMqtt(state.sessionData, action)

        intentHandlingService.intentHandling(action.intentName, action.intent)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(state.sessionData)
        )
    }

}