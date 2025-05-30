package org.rhasspy.mobile.logic.services.dialog.dialogmanager.local

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.*
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.IDialogManager
import org.rhasspy.mobile.logic.services.dialog.states.IStateTransition

internal class DialogManagerLocal(
    private val dialogManagerService: IDialogManagerService,
    private val sessionStateActions: ISessionStateActions,
    private val idleStateActions: IIdleStateActions,
    private val stateTransition: IStateTransition,
    private val playingAudioStateActions: IPlayingAudioStateActions
) : IDialogManager {

    private val logger = Logger.withTag("DialogManagerLocal")

    fun onInit() {
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = null,
                isSourceMqtt = false
            )
        )
    }

    override fun onAction(action: DialogServiceMiddlewareAction) {
        with(dialogManagerService.currentDialogState.value) {
            logger.d { "action $action on state $this" }
            when (this) {
                is SessionState      -> sessionStateActions.onAction(action, this)
                is IdleState         -> idleStateActions.onAction(action)
                is PlayingAudioState -> playingAudioStateActions.onAction(action)
            }
        }
    }

}