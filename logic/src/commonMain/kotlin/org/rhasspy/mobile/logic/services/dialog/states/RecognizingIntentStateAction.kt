package org.rhasspy.mobile.logic.services.dialog.states

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.RecognizingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.indication.IIndicationService
import org.rhasspy.mobile.logic.services.intenthandling.IIntentHandlingService

interface IRecognizingIntentStateAction {

    fun onAction(
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

    override fun onAction(
        action: DialogServiceMiddlewareAction,
        state: RecognizingIntentState
    ) {

        when (action) {
            is IntentRecognitionResult -> onIntentRecognitionResultAction(action, state)
            is IntentRecognitionError  -> onIntentRecognitionErrorAction(action, state)
            is EndSession              -> onEndSessionAction(action, state)
            else                       -> Unit
        }

    }

    private fun onEndSessionAction(
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

    private fun onIntentRecognitionErrorAction(
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

    private fun onIntentRecognitionResultAction(
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