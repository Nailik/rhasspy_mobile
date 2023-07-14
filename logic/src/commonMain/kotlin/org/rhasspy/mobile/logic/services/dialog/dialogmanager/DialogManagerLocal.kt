package org.rhasspy.mobile.logic.services.dialog.dialogmanager

import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.Source.Mqtt
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.*
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.states.*

internal class DialogManagerLocal(
    private val dialogManagerService: IDialogManagerService,
    private val idleStateAction: IIdleStateAction,
    private val recordingIntentStateAction: IRecordingIntentStateAction,
    private val audioPlayingStateAction: IAudioPlayingStateAction,
    private val transcribingIntentState: ITranscribingIntentStateAction,
    private val recognizingIntentStateAction: IRecognizingIntentStateAction,
) : IDialogManager, KoinComponent {

    override suspend fun onAction(action: DialogServiceMiddlewareAction) {
        with(dialogManagerService.currentDialogState.value) {
            val sessionIdValid = when {
                action.source is Mqtt && this.sessionData?.sessionId != null -> action.source.sessionId == this.sessionData?.sessionId
                else -> true
            }

            if (sessionIdValid) {
                when (this) {
                    is IdleState -> idleStateAction.onAction(action)
                    is RecordingIntentState -> recordingIntentStateAction.onAction(action, this)
                    is TranscribingIntentState -> transcribingIntentState.onAction(action, this)
                    is RecognizingIntentState -> recognizingIntentStateAction.onAction(action, this)
                    is AudioPlayingState -> audioPlayingStateAction.onAction(action, this)
                }
            }
        }
    }

}