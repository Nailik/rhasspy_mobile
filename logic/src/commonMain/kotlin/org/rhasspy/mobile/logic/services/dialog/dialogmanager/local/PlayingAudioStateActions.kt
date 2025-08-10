package org.rhasspy.mobile.logic.services.dialog.dialogmanager.local

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayFinished
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopAudioPlaying
import org.rhasspy.mobile.logic.middleware.Source.Mqtt
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.states.IStateTransition

interface IPlayingAudioStateActions {

    fun onAction(action: DialogServiceMiddlewareAction)

}

internal class PlayingAudioStateActions(
    private val dialogManagerService: IDialogManagerService,
    private val audioPlayingService: IAudioPlayingService,
    private val stateTransition: IStateTransition,
) : IPlayingAudioStateActions {
    override fun onAction(action: DialogServiceMiddlewareAction) {
        when (action) {
            is PlayFinished -> onPlayFinished(action)
            is StopAudioPlaying -> onStopAudioPlaying(action)
            else -> Unit
        }
    }

    private fun onPlayFinished(action: PlayFinished) {
        dialogManagerService.informMqtt(null, action)

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = null,
                isSourceMqtt = action.source is Mqtt
            )
        )
    }

    private fun onStopAudioPlaying(action: StopAudioPlaying) {
        audioPlayingService.stopPlayAudio()

        dialogManagerService.informMqtt(null, PlayFinished(action.source))

        dialogManagerService.addToHistory(action)
        dialogManagerService.transitionTo(
            state = stateTransition.transitionToIdleState(
                sessionData = null,
                isSourceMqtt = action.source is Mqtt
            )
        )
    }

}