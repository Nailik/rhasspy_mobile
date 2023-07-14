package org.rhasspy.mobile.logic.services.dialog.states

import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayFinished
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopAudioPlaying
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.AudioPlayingState

interface IAudioPlayingStateAction {

    suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: AudioPlayingState
    )

}

internal class AudioPlayingStateAction : IAudioPlayingStateAction {

    override suspend fun onAction(
        action: DialogServiceMiddlewareAction,
        state: AudioPlayingState
    ) {

        when (action) {
            is PlayFinished -> TODO()
            is StopAudioPlaying -> TODO()
            else -> Unit
        }

    }

}