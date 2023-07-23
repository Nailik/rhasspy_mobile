package org.rhasspy.mobile.logic.services.dialog.dialogmanager

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayAudio
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopAudioPlaying
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.AudioPlayingState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.IdleState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.RecognizingIntentState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.RecordingIntentState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.TranscribingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.states.IAudioPlayingStateAction
import org.rhasspy.mobile.logic.services.dialog.states.IIdleStateAction
import org.rhasspy.mobile.logic.services.dialog.states.IRecognizingIntentStateAction
import org.rhasspy.mobile.logic.services.dialog.states.IRecordingIntentStateAction
import org.rhasspy.mobile.logic.services.dialog.states.ITranscribingIntentStateAction

class DialogManagerRemoteMqtt(
    private val dialogManagerService: IDialogManagerService,
    private val idleStateAction: IIdleStateAction,
    private val recordingIntentStateAction: IRecordingIntentStateAction,
    private val audioPlayingStateAction: IAudioPlayingStateAction,
    private val transcribingIntentState: ITranscribingIntentStateAction,
    private val recognizingIntentStateAction: IRecognizingIntentStateAction,
) : IDialogManager {

    override suspend fun onAction(action: ServiceMiddlewareAction.DialogServiceMiddlewareAction) {
        with(dialogManagerService.currentDialogState.value) {
            if (checkIfActionIsAllowed(this, action)) {
                when (this) {
                    is IdleState               -> idleStateAction.onAction(action)
                    is RecordingIntentState    -> recordingIntentStateAction.onAction(action, this)
                    is TranscribingIntentState -> transcribingIntentState.onAction(action, this)
                    is RecognizingIntentState  -> recognizingIntentStateAction.onAction(
                        action,
                        this
                    )

                    is AudioPlayingState       -> audioPlayingStateAction.onAction(action, this)
                }
            }
        }
    }

    private fun checkIfActionIsAllowed(
        state: DialogManagerState,
        action: ServiceMiddlewareAction.DialogServiceMiddlewareAction
    ): Boolean {
        if (action.source is Source.Local) return true
        //session id is not null and doesn't match
        if (action.source is Source.Mqtt && state.sessionData?.sessionId != null && action.source.sessionId != state.sessionData?.sessionId) return false

        //is http api
        return when (action) {
            is WakeWordDetected,
            is PlayAudio,
            is StopAudioPlaying -> true

            else                -> false
        }

    }
}