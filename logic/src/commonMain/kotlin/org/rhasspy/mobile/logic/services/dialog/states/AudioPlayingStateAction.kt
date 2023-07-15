package org.rhasspy.mobile.logic.services.dialog.states

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayFinished
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopAudioPlaying
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.AudioPlayingState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService

interface IAudioPlayingStateAction {

    suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: AudioPlayingState
    )

}

internal class AudioPlayingStateAction(
    private val dialogManagerService: IDialogManagerService,
    private val audioPlayingService: IAudioPlayingService,
    private val stateTransition: IStateTransition
) : IAudioPlayingStateAction {

    override suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: AudioPlayingState
    ) {

        when (action) {
            is PlayFinished     -> onPlayFinishedAction(action, state)
            is StopAudioPlaying -> onStopAudioPlayingAction(action, state)
            else                -> Unit
        }

    }

    private suspend fun onPlayFinishedAction(
        action: PlayFinished,
        state: AudioPlayingState
    ) {
        dialogManagerService.informMqtt(state.sessionData, action)

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(state.sessionData)
        )
    }

    private suspend fun onStopAudioPlayingAction(
        action: StopAudioPlaying,
        state: AudioPlayingState
    ) {
        audioPlayingService.stopPlayAudio()

        dialogManagerService.informMqtt(state.sessionData, PlayFinished(Source.Local))

        dialogManagerService.transitionTo(
            action = action,
            state = stateTransition.transitionToIdleState(state.sessionData)
        )
    }

}